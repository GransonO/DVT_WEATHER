package com.granson.dvtweather.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.granson.dvtweather.data.models.LocationDetails
import com.granson.dvtweather.presentation.composables.screens.maps.MapsScreen
import com.granson.dvtweather.presentation.composables.screens.viewModels.ScreensViewModel
import com.granson.dvtweather.presentation.composables.screens.weather.WeatherScreen
import com.granson.dvtweather.presentation.navigation.DVTScreens
import com.granson.dvtweather.presentation.navigation.Screen
import com.granson.dvtweather.ui.theme.DVTWeatherTheme
import com.granson.dvtweather.utils.Common.baseLogger
import com.granson.dvtweather.utils.Common.currentTemp
import com.granson.dvtweather.utils.Common.dailyWeather
import com.granson.dvtweather.utils.Common.daysFlow
import com.granson.dvtweather.utils.Common.daysOrder
import com.granson.dvtweather.utils.Common.getWeatherEnum
import com.granson.dvtweather.utils.Common.mainWeatherEnums
import com.granson.dvtweather.utils.Common.selectedPlaceWeatherEnums
import com.granson.dvtweather.utils.Common.selectedWeatherEnums
import com.granson.dvtweather.utils.Common.userCurrentLocation
import com.granson.dvtweather.utils.Common.userLocationDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val neededPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var locationCount = 0
        daysOrder.value  = daysFlow()

        setContent {
            DVTWeatherTheme {

                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val starterState = remember { mutableStateOf(true) }
                val fetchingMessage = remember { mutableStateOf("Fetching your current location, gimme a sec...") }

                val weatherState = remember { mutableStateOf(selectedWeatherEnums.value) }
                val screensViewModel = hiltViewModel<ScreensViewModel>()

                val current = {
                    scope.launch {
                        screensViewModel.getWeatherInfo(
                            lat = userCurrentLocation.latitude.toFloat(),
                            lon = userCurrentLocation.longitude.toFloat(),
                            context = context
                        )
                        screensViewModel.currentWeather.collect {
                            baseLogger("Anything yet", it.toString())
                            val value = it.data
                            if(value != null){
                                currentTemp.value = value.current.temp.roundToInt().toString()
                                weatherState.value = getWeatherEnum(value.current.weather[0].id)
                                dailyWeather.value = value.daily
                                selectedWeatherEnums.value = weatherState.value
                                mainWeatherEnums.value = selectedWeatherEnums.value // Never Changing
                                selectedPlaceWeatherEnums.value = selectedWeatherEnums.value // Change with selected Place

                                delay(2000)
                                mainViewModel.isLocationAcquired.value = true // Navigate to Home page

                            }
                        }
                    }
                }

                val makeLocationRequest = {
                    scope.launch {
                        // Fetch Current Location
                        delay(2000)
                        mainViewModel.getCurrentLocation().collectLatest {
                            if(locationCount == 0){
                                locationCount = 1
                                userCurrentLocation = LatLng(it.latitude, it.longitude)
                                geoCodeLocation(context, it.latitude, it.longitude)
                                fetchingMessage.value = "Got the location,\n${userLocationDetails.value.address}\nHang in there, sourcing for weather info..."

                                current.invoke()
                            }
                        }
                    }
                }

                val checkUserPermissions = {
                    scope.launch {
                        delay(2000)
                        when {
                            hasPermissions(context, *neededPermissions) -> {
                                // All permissions granted
                                starterState.value = false //Navigate from Starter Page
                                mainViewModel.hasPermissions.value = true // Has all permissions given

                            }
                            else -> {
                                // Request permissions
                                starterState.value = false //Navigate from Starter Page
                                mainViewModel.hasPermissions.value = false
                            }
                        }
                    }
                }

                if(starterState.value){
                    StarterPage(
                        firstText = "",
                        secondText = "",
                        headerText = "DVT Weather",
                        showButton = false
                    )

                    checkUserPermissions.invoke()
                }else{
                    if(mainViewModel.isLocationAcquired.value){
                        HomePage()
                    }else{
                        if(mainViewModel.hasPermissions.value){
                            // Permissions granted
                            StarterPage(
                                firstText = "Hello there,",
                                secondText = fetchingMessage.value,
                                showButton = false
                            )
                            makeLocationRequest.invoke() // Get current location

                        }else{
                            // First Timer
                            StarterPage(
                                firstText = "Lets get you started.",
                                secondText = "Click on the button below to get your weather details."
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StarterPage(
        firstText: String,
        secondText: String,
        showButton: Boolean = true,
        headerText: String = "DVT Weather,"
    ){

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { maps ->
            mainViewModel.hasPermissions.value = maps.values.reduce { acc, next -> (acc && next) }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){
            Box(
                modifier = Modifier.fillMaxSize().background(color = backgroundColorExt(selectedWeatherEnums.value)),
                contentAlignment = Alignment.BottomStart
            ){
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(imageToDisplay(selectedWeatherEnums.value)),
                    contentDescription = "Back Image",
                    alignment = Alignment.TopStart,
                    contentScale = ContentScale.FillWidth
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    ) {

                        Text(
                            modifier = Modifier.padding(top = 55.dp),
                            text = headerText,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 35.sp
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = 25.dp),
                            text = firstText,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 25.sp
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = secondText,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                    }

                    if (showButton)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 55.dp).clickable {
                                 launcher.launch(neededPermissions)
                            },
                            backgroundColor = Color.White,
                            border = BorderStroke(
                                width = 2.dp,
                                color = backgroundColorExt(selectedWeatherEnums.value),
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ){
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                                text = "Let's Start",
                                style = TextStyle(
                                    color = backgroundColorExt(selectedWeatherEnums.value),
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    else
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(5.dp),
                                color = Color.White
                            )
                        }

                }
            }
        }
    }

    @Composable
    fun HomePage(){

        val navController = rememberNavController()
        val scaffoldStateR = rememberScaffoldState()
        LaunchedEffect("Update MapScope Theme"){
            selectedPlaceWeatherEnums.value = selectedWeatherEnums.value
        }

        Scaffold(
            scaffoldState = scaffoldStateR,
            bottomBar = {
                BottomNavigation(
                    backgroundColor = backgroundColor(selectedWeatherEnums.value)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    DVTScreens.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        BottomNavigationItem(
                            icon = { Icon(painterResource(screen.Icon), contentDescription = null, tint = if(isSelected) Color.White else Color.Black) },
                            label = { Text(
                                text = stringResource(screen.stringId),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = if(isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        )
                    }
                }
            }
        ){
            NavHost(navController, startDestination = Screen.WeatherScreen.route, Modifier) {
                composable(Screen.WeatherScreen.route) { WeatherScreen(navController) }
                composable(Screen.MapsScreen.route) { MapsScreen(navController) }
            }
        }
    }

    private fun hasPermissions(
        context: Context,
        vararg permissions: String
    ): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun geoCodeLocation(context: Context, latitude: Double, longitude: Double){
        val geocoder = Geocoder(context, Locale.getDefault())

        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
        val address: String = addresses[0].subLocality ?: addresses[0].getAddressLine(0)
        val city: String = addresses[0].locality
        val locality: String = addresses[0].locality
        val country: String = addresses[0].countryName

        baseLogger("The address", address)
        userLocationDetails.value = LocationDetails(
            address = address.split(",")[0],
            city = city,
            locality = locality,
            country = country
        )

    }
}
