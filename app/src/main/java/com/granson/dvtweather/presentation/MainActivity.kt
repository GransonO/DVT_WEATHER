package com.granson.dvtweather.presentation

import android.Manifest
import android.annotation.SuppressLint
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
import com.granson.dvtweather.R
import com.granson.dvtweather.data.models.LocationDetails
import com.granson.dvtweather.presentation.composables.backgroundColor
import com.granson.dvtweather.presentation.composables.backgroundColorExt
import com.granson.dvtweather.presentation.composables.imageToDisplay
import com.granson.dvtweather.presentation.composables.screens.maps.MapsScreen
import com.granson.dvtweather.presentation.composables.screens.viewModels.ScreensViewModel
import com.granson.dvtweather.presentation.composables.screens.weather.WeatherScreen
import com.granson.dvtweather.presentation.navigation.DVTScreens
import com.granson.dvtweather.presentation.navigation.Screen
import com.granson.dvtweather.ui.theme.DVTWeatherTheme
import com.granson.dvtweather.ui.theme.Typography
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
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
                val isOffline = remember { mutableStateOf(false) }
                val starterState = remember { mutableStateOf(true) }
                val hasInternet = remember { mutableStateOf(true) }
                val noLocation = remember { mutableStateOf(false) }
                val fetchingMessage = remember { mutableStateOf("...") }

                val weatherState = remember { mutableStateOf(selectedWeatherEnums.value) }
                val screensViewModel = hiltViewModel<ScreensViewModel>()

                LaunchedEffect(key1 = "Fetch Saved Places"){
                    // Fetch List
                    screensViewModel.getSavedPlaces()
                }

                val current = {
                    scope.launch {

                        screensViewModel.getWeatherInfo(
                            lat = userCurrentLocation.latitude.toFloat(),
                            lon = userCurrentLocation.longitude.toFloat(),
                            context.resources.getString(R.string.weather_api_key)
                        )
                        screensViewModel.currentWeather.collect {
                                val value = it.data
                                if(value != null){
                                    currentTemp.value = value.current.temp.roundToInt().toString()
                                    weatherState.value = getWeatherEnum(value.current.weather[0].id)
                                    dailyWeather.value = value.daily
                                    selectedWeatherEnums.value = weatherState.value
                                    mainWeatherEnums.value = selectedWeatherEnums.value // Never Changing
                                    selectedPlaceWeatherEnums.value = selectedWeatherEnums.value // Change with selected Place

                                    delay(2000)
                                    isOffline.value = false
                                    mainViewModel.isLocationAcquired.value = true // Navigate to Home page

                                }
                            }
                    }
                }

                val makeLocationRequest = {
                    scope.launch {
                        // Fetch Current Location
                        fetchingMessage.value = "Fetching your current location, gimme a sec..."
                        delay(2000)

                        if(screensViewModel.getInternetStatus(context)){
                            if(mainViewModel.locationEnabled.invoke()){
                                mainViewModel.getCurrentLocation().collectLatest {
                                    if(locationCount == 0){
                                        locationCount = 1
                                        userCurrentLocation = LatLng(it.latitude, it.longitude)
                                        CoroutineScope(Dispatchers.IO).launch {
                                            geoCodeLocation(context, it.latitude, it.longitude){
                                                fetchingMessage.value = "Got the location,\n\n${userLocationDetails.value.address}\n\nHang in there, sourcing for weather info..."
                                                current.invoke()
                                            }
                                        }
                                    }
                                }   
                            }else{
                                fetchingMessage.value = "Location!,\nPlease enable your Location to proceed "
                                noLocation.value = false
                            }
                        }else{
                            fetchingMessage.value = "Oops, looks like there no Internet connection! Please check your network and try again"
                            hasInternet.value = false
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

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { maps ->
                    mainViewModel.hasPermissions.value = maps.values.reduce { acc, next -> (acc && next) }
                }

                if(starterState.value){
                    StarterPage(
                        firstText = "",
                        secondText = "",
                        headerText = "DVT Weather",
                        showButton = false
                    )

                    checkUserPermissions.invoke()
                } else {
                    if(mainViewModel.isLocationAcquired.value){
                        HomePage()

                    }else{
                        if(mainViewModel.hasPermissions.value){
                            // Permissions granted
                            if(!hasInternet.value){
                                when{
                                    screensViewModel.listSavedPlaces.value.isNotEmpty() -> {
                                        StarterPage(
                                            firstText = "Oh,",
                                            secondText = "Looks like there's no Internet connection! Would you like to start offline mode?",
                                            showButton = true,
                                            buttonText = "Lets go offline",
                                            onClick = {
                                                isOffline.value = true
                                            }
                                        )
                                    }
                                    else -> {
                                        StarterPage(
                                            firstText = "Oh,",
                                            secondText = fetchingMessage.value,
                                            showButton = true,
                                            buttonText = "Retry",
                                            onClick = {
                                                makeLocationRequest.invoke()
                                            }
                                        )
                                    }
                                }

                            }else{
                                when{
                                    noLocation.value ->{
                                        StarterPage(
                                            firstText = "Oh,",
                                            secondText = fetchingMessage.value,
                                            showButton = true,
                                            buttonText = "Retry",
                                            onClick = {
                                                makeLocationRequest.invoke()
                                            }
                                        )
                                    }
                                    else -> {
                                        StarterPage(
                                            firstText = "Hello there,",
                                            secondText = fetchingMessage.value,
                                            showButton = false
                                        )
                                        if(locationCount != 1){
                                            // Invoke once upon render
                                            makeLocationRequest.invoke() // Get current location
                                        }
                                    }
                                }
                            }

                        }else{
                            // First Timer
                            StarterPage(
                                firstText = "Lets get you started.",
                                secondText = "Click on the button below to get your weather details.",
                                onClick = {
                                    launcher.launch(neededPermissions)
                                }
                            )
                        }
                    }
                }

                if(isOffline.value){
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ){
                        MapsScreen(
                            isOffline = true,
                            onLineCall = { makeLocationRequest.invoke() }
                        )
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
        buttonText: String = "Let's Start",
        headerText: String = "DVT Weather,",
        onClick: () -> Unit = {}
    ){
        Column (
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
                            style = Typography.body1.copy(
                                color = Color.White,
                                fontSize = 35.sp,
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = 25.dp),
                            text = firstText,
                            style = Typography.body1.copy(
                                color = Color.White,
                                fontSize = 25.sp
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = secondText,
                            style = Typography.body1.copy(
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                    }

                    if (showButton)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 55.dp, start = 16.dp, end = 16.dp).clickable {
                                 onClick.invoke()
                            },
                            backgroundColor = Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = backgroundColorExt(selectedWeatherEnums.value),
                            ),
                            shape = RoundedCornerShape(50.dp)
                        ){
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                text = buttonText,
                                style = Typography.body1.copy(
                                    color = backgroundColorExt(selectedWeatherEnums.value),
                                    fontSize = 20.sp,
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

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun HomePage(){

        val navController = rememberNavController()
        LaunchedEffect("Update MapScope Theme"){
            selectedPlaceWeatherEnums.value = selectedWeatherEnums.value
        }

        Scaffold(
            backgroundColor = backgroundColorExt(selectedWeatherEnums.value),
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
                                style = Typography.body1.copy(
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
            Box(
                modifier = Modifier.fillMaxSize()
            ){

                Image(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    painter = painterResource(imageToDisplay(selectedWeatherEnums.value)),
                    contentDescription = "Back Image",
                    alignment = Alignment.TopStart,
                    contentScale = ContentScale.FillWidth
                )

                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                NavHost(navController, startDestination = Screen.WeatherScreen.route, Modifier) {
                    composable(Screen.WeatherScreen.route) { WeatherScreen() }
                    composable(Screen.MapsScreen.route) { MapsScreen(isOffline = false){  } }
                }
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

    private fun geoCodeLocation(context: Context, latitude: Double, longitude: Double, afterCall: ()-> Unit){

        val geocoder = Geocoder(context, Locale.getDefault())

        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
        val address: String = addresses[0].subLocality ?: addresses[0].getAddressLine(0)
        val city: String = addresses[0].locality
        val locality: String = addresses[0].locality
        val country: String = addresses[0].countryName

        userLocationDetails.value = LocationDetails(
            address = address.split(",")[0],
            city = city,
            locality = locality,
            country = country
        )

        afterCall.invoke()
    }
}
