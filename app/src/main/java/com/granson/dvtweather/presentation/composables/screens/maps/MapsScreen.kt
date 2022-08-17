package com.granson.dvtweather.presentation.composables.screens.maps

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.granson.dvtweather.R
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import com.granson.dvtweather.enums.PlacesSheetEnums
import com.granson.dvtweather.presentation.composables.*
import com.granson.dvtweather.presentation.composables.screens.viewModels.ScreensViewModel
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.PlaceLocation
import com.granson.dvtweather.ui.theme.Typography
import com.granson.dvtweather.utils.Common
import com.granson.dvtweather.utils.Common.baseLogger
import com.granson.dvtweather.utils.Common.getCurrentDate
import com.granson.dvtweather.utils.Common.getWeatherEnum
import com.granson.dvtweather.utils.Common.mainWeatherEnums
import com.granson.dvtweather.utils.Common.selectedPlaceWeatherEnums
import com.granson.dvtweather.utils.Common.selectedWeatherEnums
import com.granson.dvtweather.utils.Common.userCurrentLocation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(
    isOffline: Boolean = false,
    onLineCall: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed)
    val sheetState = remember { mutableStateOf(PlacesSheetEnums.ADD) }
    val listView = remember { mutableStateOf(true) }
    val screensViewModel = hiltViewModel<ScreensViewModel>()
    val context = LocalContext.current

    LaunchedEffect(key1 = "Fetch Saved Places"){
        // Fetch List
        screensViewModel.getSavedPlaces()
    }

    val openSheet = {
        scope.launch {
            backdropScaffoldState.animateTo(BackdropValue.Concealed)
        }
    }

    if(backdropScaffoldState.currentValue == BackdropValue.Concealed){
        selectedWeatherEnums.value = selectedPlaceWeatherEnums.value
    }else{
        selectedWeatherEnums.value = mainWeatherEnums.value
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        BackdropScaffold(
            scaffoldState = backdropScaffoldState,
            appBar = {

            },
            peekHeight = 25.dp,
            headerHeight = 0.dp,
            backLayerBackgroundColor = backgroundColorExt(
                if(backdropScaffoldState.currentValue == BackdropValue.Concealed) selectedPlaceWeatherEnums.value else selectedWeatherEnums.value
            ),
            backLayerContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ){
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(imageToDisplay(selectedWeatherEnums.value)),
                        contentDescription = "Back Image",
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.FillWidth
                    )

                    val width = LocalConfiguration.current.screenWidthDp

                    Column(
                        modifier = Modifier.fillMaxSize().padding(
                            top = 10.dp,
                            bottom = 10.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 35.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier,
                                text = if(isOffline) "Offline Mode" else "Favourite Places",
                                style = Typography.body1.copy(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            if(!isOffline)
                                Card(
                                    modifier = Modifier.clickable{
                                        sheetState.value = PlacesSheetEnums.ADD
                                        openSheet.invoke()
                                    },
                                    shape = RoundedCornerShape(25.dp),
                                    backgroundColor = Color.White,
                                    elevation = 10.dp
                                ) {
                                    Box(
                                        modifier = Modifier.padding(5.dp),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Icon(Icons.Rounded.Add, "", tint = backgroundColorExt(selectedPlaceWeatherEnums.value))
                                    }
                                }
                        }

                        if(!isOffline)
                            Row(
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                horizontalArrangement = Arrangement.Center
                            ){
                                Box(
                                    modifier = Modifier.width((width * 0.45).dp).height(45.dp).clip(shape = RoundedCornerShape(10.dp)).background(
                                        color = if(listView.value) backgroundColor(
                                            selectedPlaceWeatherEnums.value
                                        ).copy(alpha = 0.3f) else Color.Transparent).clickable {
                                        listView.value = true
                                    },
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        text = "List",
                                        style = Typography.body1.copy(
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier.width((width * 0.45).dp).height(45.dp).clip(shape = RoundedCornerShape(10.dp)).background(
                                        color = if(!listView.value) backgroundColor(selectedPlaceWeatherEnums.value).copy(alpha = 0.3f) else Color.Transparent
                                    ).clickable {
                                        listView.value = false
                                    },
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        text = "Map",
                                        style = Typography.body1.copy(
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                        if(listView.value)
                            if(screensViewModel.isLoading.value){
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }else{
                                Column(
                                    modifier = Modifier.fillMaxSize().verticalScroll(
                                        rememberScrollState())
                                ){
                                    ListView(
                                        screensViewModel.listSavedPlaces.value
                                    ) {
                                        sheetState.value = PlacesSheetEnums.VIEW
                                        screensViewModel.selectedPlace.value = it
                                        openSheet.invoke()
                                    }
                                }
                            }

                        else
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ){
                                MapView(
                                    screensViewModel.listSavedPlaces.value
                                ){
                                    if(it.name != ""){
                                        sheetState.value = PlacesSheetEnums.VIEW
                                        screensViewModel.selectedPlace.value = it
                                        openSheet.invoke()
                                    }
                                }
                            }
                    }
                }
            },
            frontLayerContent = {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = 13.dp,
                    contentColor = backgroundColorExt(selectedPlaceWeatherEnums.value)
                ){
                    Box(
                        modifier = Modifier.fillMaxSize().background(color = backgroundColorExt(selectedPlaceWeatherEnums.value)),
                        contentAlignment = Alignment.BottomStart
                    ){
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResource(imageToDisplay(selectedPlaceWeatherEnums.value)),
                            contentDescription = "Back Image",
                            alignment = Alignment.TopStart,
                            contentScale = ContentScale.FillWidth,
                            alpha = if (selectedPlaceWeatherEnums.value == selectedWeatherEnums.value) 0.3f else 1f
                        )

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(4.dp))
                                    .width(48.dp)
                                    .background(Color.White)
                                    .height(4.dp)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            when(sheetState.value){
                                PlacesSheetEnums.ADD -> {
                                    AddLocation(
                                        screensViewModel = screensViewModel,
                                        showMap = backdropScaffoldState.currentValue == BackdropValue.Concealed
                                    )
                                }
                                PlacesSheetEnums.VIEW -> {
                                    LocationView(
                                        screensViewModel = screensViewModel,
                                        showMap = backdropScaffoldState.currentValue == BackdropValue.Concealed,
                                        placeItem = screensViewModel.selectedPlace.value
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        if(screensViewModel.getInternetStatus(context) &&  isOffline ){
            Card(
                modifier = Modifier.fillMaxWidth().align(alignment = Alignment.BottomCenter).padding(bottom = 5.dp, start = 16.dp, end = 16.dp).clickable {
                    onLineCall.invoke()
                },
                backgroundColor = backgroundColorExt(selectedWeatherEnums.value),
                border = BorderStroke(
                    width = 1.dp,
                    color = backgroundColorExt(selectedWeatherEnums.value),
                ),
                shape = RoundedCornerShape(10.dp)
            ){
                Text(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    text = "Network available, go online",
                    style = Typography.body1.copy(
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

@Composable
fun MapView(
    list: List<SavedPlace>,
    openSheet: (SavedPlace) -> Unit
){
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState(key = Random.toString())  {
        position = CameraPosition.fromLatLngZoom(userCurrentLocation, 13f)
    }

    val builder = LatLngBounds.Builder()
    list.forEach { place ->
        val allLocations = LatLng(place.location.latitude, place.location.longitude)
        builder.include(allLocations)
    }

    val refreshMarker = {
        scope.launch {
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(builder.build(), 64))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(10.dp)),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            if(list.isNotEmpty()){
                refreshMarker.invoke()
            }
        }
    ) {
        if(list.isEmpty()){
            baseLogger("The List is Okay", list)
            Marker(
                position = LatLng(userCurrentLocation.latitude, userCurrentLocation.longitude),
                title = "Current Location",
                onInfoWindowClick = {
                    openSheet.invoke(SavedPlace())
                }
            )
        }
        else{
            for (marker in list){
                Marker(
                    position = LatLng(marker.location.latitude, marker.location.longitude),
                    title = marker.name,
                    snippet = "Tap for more info",
                    onInfoWindowClick = {
                        openSheet.invoke(marker)
                    }
                )
            }
        }
    }
}

@Composable
fun ListView(
    list: List<SavedPlace>,
    openSheet: (SavedPlace) -> Unit
){
    Column {
        if(list.isEmpty()){
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(
                    modifier = Modifier.padding(top = 30.dp),
                    text = "You got no favourites, you can add one by clicking the circular button on the top right corner :)",
                    style = Typography.body1.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }else{
            for(item in list){
                LocationCard(
                    placeItem = item,
                    openSheet = {
                        openSheet.invoke(it)
                    },
                )
            }
        }
    }
}

@Composable
fun LocationCard(
    placeItem: SavedPlace,
    openSheet : (SavedPlace) -> Unit,
){
    Card(
        modifier = Modifier.fillMaxSize().padding(top = 10.dp).clickable {
            openSheet.invoke(placeItem)
        },
        shape = RoundedCornerShape(15.dp),
        backgroundColor = Color.White,
        elevation = 10.dp
    ){
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    modifier = Modifier,
                    text = placeItem.name,
                    style = Typography.body1.copy(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Box(
                    modifier = Modifier.size(10.dp).clip(shape = RoundedCornerShape(20.dp))
                ){}
            }

            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(weatherIcon(getWeatherEnum(placeItem.lastWeatherID))),
                    contentDescription = "Weather",
                    colorFilter = ColorFilter.tint(color = Color.Black)
                )

                Text(
                    modifier = Modifier,
                    text = "last updated on ${placeItem.date}",
                    style = Typography.body1.copy(
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun LocationView(
    screensViewModel: ScreensViewModel,
    showMap: Boolean = false,
    placeItem: SavedPlace
){

    baseLogger("The Place Item Is",  placeItem)
    val weatherRequest = remember { mutableStateOf(WeatherRequest()) }
    val hasWeatherValues = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val hasRunOnce = remember {mutableStateOf(false)}

    val fetchWeather = {
        scope.launch {
            if(screensViewModel.getInternetStatus(context)){
                baseLogger("Going Online", "Online")
                // Going offline
                // if(!hasRunOnce.value){
                    screensViewModel.getWeatherInfo(
                        lat = placeItem.location.latitude.toFloat(),
                        lon = placeItem.location.longitude.toFloat(),
                        context = context
                    )
                    hasRunOnce.value = true
                    screensViewModel.currentWeather.collect {
                        val value = it.data
                        if(value != null){
                            weatherRequest.value = value
                            selectedPlaceWeatherEnums.value = getWeatherEnum(value.current.weather[0].id)
                            hasWeatherValues.value = true

                            val updateItem = placeItem.copy(placeWeather = value, date = getCurrentDate.invoke())
                            screensViewModel.updatePlace(updateItem)
                        }
                    }
                // }
            }else{
                baseLogger("Going offline", "Offline")
                baseLogger("Going offline",  placeItem)
                // Going offline
                weatherRequest.value = placeItem.placeWeather!!
                selectedPlaceWeatherEnums.value = getWeatherEnum(placeItem.lastWeatherID)
                hasWeatherValues.value = true
            }
        }
    }
    fetchWeather.invoke()

    val markerPosition = LatLng(placeItem.location.latitude,placeItem.location.longitude)

    if (hasWeatherValues.value)
        Column (
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(
                    modifier = Modifier.width(90.dp)
                ){
                    Image(
                        modifier = Modifier.size(15.dp).align(alignment = Alignment.TopEnd),
                        painter = painterResource(R.drawable.ic_outline_circle_42),
                        contentDescription = "degrees"
                    )

                    Text(
                        modifier = Modifier.align(alignment = Alignment.BottomCenter).padding(top = 10.dp),
                        text = weatherRequest.value.current.temp.roundToInt().toString(),
                        style = Typography.body1.copy(
                            color = Color.White,
                            fontSize = 45.sp,
                            shadow = Shadow(
                                color = Color.LightGray,
                                offset = Offset(1f,0f),
                                blurRadius = 2f
                            )
                        )
                    )
                }

                Text(
                    text = getWeatherEnum(weatherRequest.value.current.weather[0].id).name,
                    style = Typography.body1.copy(
                        color = Color.White,
                        fontSize = 30.sp
                    )
                )

                Text(
                    text = "at ${placeItem.name}, ${placeItem.locality}",
                    style = Typography.body1.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )

            }

            Card(
                modifier = Modifier.fillMaxWidth().height(205.dp).padding(horizontal = 16.dp, vertical = 16.dp).clip(RoundedCornerShape(15.dp)),
                backgroundColor = Color.White
            ) {
                if (showMap)
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = rememberCameraPositionState(key = Random.toString())  {
                            position = CameraPosition.fromLatLngZoom(markerPosition, 13f)
                        }
                    ) {
                        Marker(
                            position = LatLng(placeItem.location.latitude, placeItem.location.longitude),
                            title = "${placeItem.name}, ${placeItem.locality}"
                        )
                    }
            }

            Box(
                modifier = Modifier.background(color = Color.Gray.copy(alpha = 0.4f))
            ){
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){

                        val todayWeather = weatherRequest.value.daily[0].temp
                        TempDisplay("${todayWeather.min.roundToInt()}","Min")

                        TempDisplay(weatherRequest.value.current.temp.roundToInt().toString(), "Current")

                        TempDisplay("${todayWeather.max.roundToInt()}", "Max")

                    }

                    Divider(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        color = Color.White,
                        thickness = 1.dp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(
                                rememberScrollState()
                            ),
                    ) {
                        weatherRequest.value.daily.forEach {
                            DateDisplay(
                                date = Common.daysOrder.value[weatherRequest.value.daily.indexOf(it)],
                                weather = getWeatherEnum(it.weather[0].id),
                                temp = it.temp.max.roundToInt().toString()
                            )
                        }

                        if(screensViewModel.listSavedPlaces.value.contains(placeItem)){
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp).clickable{
                                    screensViewModel.deletePlace(placeItem)
                                },
                                backgroundColor = Color.White,
                                shape = RoundedCornerShape(100.dp),
                                border = BorderStroke(
                                    width = 2.dp,
                                    color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                                )
                            ){
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                                    text = "REMOVE PLACE",
                                    style = Typography.body1.copy(
                                        color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(Modifier.height(55.dp))

                    }
                }
            }
        }
    else
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
    // Display Toasts
    if(screensViewModel.dbRequestError.value){
        Toast.makeText(context, "An error occurred while performing the request", Toast.LENGTH_LONG).show()
        screensViewModel.dbRequestError.value = false
    }

    if(screensViewModel.isDeleted.value){
        Toast.makeText(context, "${placeItem.name} removed from favourites", Toast.LENGTH_LONG).show()
        screensViewModel.isDeleted.value = false
    }
}

@Composable
fun AddLocation(
    screensViewModel: ScreensViewModel,
    showMap: Boolean = false
){

    val markerPosition = remember { mutableStateOf(userCurrentLocation) }
    val scope = rememberCoroutineScope()
    val fetchingMessage = remember { mutableStateOf("Getting the place details") }
    val isFetchingDetails = remember { mutableStateOf(true) }
    val selectedPlace = remember { mutableStateOf(SavedPlace()) }
    val context = LocalContext.current

    val weatherRequest = remember { mutableStateOf(WeatherRequest()) }

    if(!screensViewModel.isPlaceSelected.value){
        selectedPlaceWeatherEnums.value = selectedWeatherEnums.value
    }

    val requestWeatherDetails = {
        scope.launch {
            screensViewModel.getWeatherInfo(
                lat = selectedPlace.value.location.latitude.toFloat(),
                lon = selectedPlace.value.location.longitude.toFloat(),
                context = context
            )

            screensViewModel.currentWeather.collect {
                val value = it.data
                if(value != null){
                    baseLogger("Anything yet", it.toString())
                    weatherRequest.value = value
                    selectedPlace.value = selectedPlace.value.copy(
                        placeWeather = value,
                        lastWeatherID = value.current.weather[0].id,
                        date = getCurrentDate.invoke()
                    )
                    selectedPlaceWeatherEnums.value = getWeatherEnum(value.current.weather[0].id)
                    baseLogger("Selected Weather", value)
                    isFetchingDetails.value = false
                }
            }
        }
    }

    val requestPlaceWeatherDetails = {
        scope.launch {
            screensViewModel.getPlaceDetails(
                placeId = selectedPlace.value.placeId,
                context
            )
            screensViewModel.placeDetails.collectLatest {
                fetchingMessage.value = "Got the place, ${it.name}\nFetching weather details"
                selectedPlace.value = selectedPlace.value.copy(
                    location = PlaceLocation(it.location.latitude, it.location.longitude),
                    placeId = selectedPlace.value.placeId,
                )
                markerPosition.value = LatLng(selectedPlace.value.location.latitude,selectedPlace.value.location.longitude)
                requestWeatherDetails.invoke()
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ){
        if(!screensViewModel.isPlaceSelected.value){
            DVTEditText(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                fieldPlaceholder = "Search Place",
                onValueChanged = {
                    if (it.length > 4){
                        screensViewModel.isRequesting.value = true
                        screensViewModel.placeSearch(
                            it,
                            context
                        )
                    }
                }
            )
        }else{
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ){
                Card(
                    modifier = Modifier.clickable{
                        screensViewModel.isPlaceSelected.value = false
                    },
                    shape = RoundedCornerShape(25.dp),
                    backgroundColor = Color.White,
                    elevation = 10.dp
                ) {
                    Box(
                        modifier = Modifier.padding(5.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(Icons.Rounded.Close, "", tint = backgroundColorExt(selectedPlaceWeatherEnums.value))
                    }
                }
            }
        }

        if(screensViewModel.isRequesting.value){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = Color.White,
            )
        }

        if(screensViewModel.isPlaceSelected.value){
            if(isFetchingDetails.value){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                            text = fetchingMessage.value,
                            style = Typography.body1.copy(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }else{
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(205.dp).padding(start = 16.dp, end = 16.dp, top = 16.dp).clip(RoundedCornerShape(15.dp)),
                        backgroundColor = Color.White
                    ) {
                        if(showMap){
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(userCurrentLocation, 15f)
                            }

                            val refreshMarker = {
                                scope.launch {
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(markerPosition.value, 15f)
                                }
                            }

                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                onMapLoaded = {
                                    refreshMarker.invoke()
                                }
                            ) {
                                Marker(
                                    position = markerPosition.value,
                                    title = selectedPlace.value.name,
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.padding(bottom = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ){
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResource(imageToDisplay(selectedPlaceWeatherEnums.value)),
                            contentDescription = "Back Image",
                            alignment = Alignment.TopStart,
                            contentScale = ContentScale.FillWidth
                        )

                        CurrentWeatherCol(
                            16.dp,
                            weatherRequest.value.current.temp.roundToInt().toString(),
                            selectedPlaceWeatherEnums.value,
                            selectedPlace.value.name
                        )
                    }

                    Box(
                        modifier = Modifier.background(color = backgroundColorExt(selectedPlaceWeatherEnums.value).copy(alpha = 0.4f))
                    ){
                        Column {

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                if(
                                    weatherRequest.value.daily.isNotEmpty()
                                ){
                                    val todayWeather = weatherRequest.value.daily[0].temp
                                    TempDisplay("${todayWeather.min.roundToInt()}","Min")

                                    TempDisplay(weatherRequest.value.current.temp.roundToInt().toString(), "Current")

                                    TempDisplay("${todayWeather.max.roundToInt()}", "Max")
                                }

                            }

                            Divider(
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                color = Color.White,
                                thickness = 1.dp
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                weatherRequest.value.daily.forEach {
                                    DateDisplay(
                                        date = Common.daysOrder.value[weatherRequest.value.daily.indexOf(it)],
                                        weather = getWeatherEnum(it.weather[0].id),
                                        temp = it.temp.max.roundToInt().toString()
                                    )
                                }
                            }
                        }
                    }

                    if(!screensViewModel.listSavedPlaces.value.contains(selectedPlace.value)){
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp).clickable {
                                screensViewModel.savePlace(selectedPlace.value)
                            },
                            backgroundColor = Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                            ),
                            shape = RoundedCornerShape(100.dp),
                        ){
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                                text = "Add to favourites",
                                style = Typography.body1.copy(
                                    color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }else{
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    if(screensViewModel.isAddedSuccess.value){
                        Toast.makeText(context, "Place Added success", Toast.LENGTH_LONG).show()
                        screensViewModel.isAddedSuccess.value = false
                    }

                }
            }
        }else{
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val predictions = screensViewModel.queryPlaces.value.predictions
                if(predictions.isNotEmpty()){
                    predictions.forEach {
                        PlaceItem(
                            SavedPlace(
                                name = it.description,
                                placeId = it.placeId
                            )
                        ){ v ->
                            screensViewModel.isPlaceSelected.value = true
                            selectedPlace.value = v
                            requestPlaceWeatherDetails.invoke()
                        }
                        if(predictions.indexOf(it) < predictions.size - 1){
                            Divider(
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp, start = 16.dp, end = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }

    }
    // Display Toasts
    if(screensViewModel.requestError.value){
        Toast.makeText(context, "Request Erroe, please try again later", Toast.LENGTH_LONG).show()
        screensViewModel.requestError.value = false
    }
}

@Composable
fun PlaceItem(
    placeDetails: SavedPlace,
    isSelected: (SavedPlace) -> Unit
){
    Column {
        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 25.dp, start = 16.dp, end = 16.dp).clickable {
                isSelected(placeDetails)
            },
            text = placeDetails.name,
            style = Typography.body1.copy(
                color = Color.White,
                fontSize = 16.sp
            )
        )
    }
}