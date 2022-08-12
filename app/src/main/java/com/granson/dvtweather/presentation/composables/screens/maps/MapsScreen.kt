package com.granson.dvtweather.presentation.composables.screens.maps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.granson.dvtweather.R
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import com.granson.dvtweather.enums.PlacesSheetEnums
import com.granson.dvtweather.presentation.*
import com.granson.dvtweather.presentation.composables.screens.viewModels.ScreensViewModel
import com.granson.dvtweather.utils.Common
import com.granson.dvtweather.utils.Common.baseLogger
import com.granson.dvtweather.utils.Common.getWeatherEnum
import com.granson.dvtweather.utils.Common.mainWeatherEnums
import com.granson.dvtweather.utils.Common.selectedPlaceWeatherEnums
import com.granson.dvtweather.utils.Common.selectedWeatherEnums
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed)
    val sheetState = remember { mutableStateOf(PlacesSheetEnums.ADD) }
    val listView = remember { mutableStateOf(true) }
    val screensViewModel = hiltViewModel<ScreensViewModel>()

    LaunchedEffect(key1 = "Fetch Saved Places"){
        // Fetch List
        screensViewModel.getSavedPlaces()
    }

    val openSheet = {
        scope.launch {
            backdropScaffoldState.animateTo(BackdropValue.Concealed)
        }
    }

    val closeSheet = {
        scope.launch {
            backdropScaffoldState.reveal()
        }
    }

    if(backdropScaffoldState.currentValue == BackdropValue.Concealed){
        selectedWeatherEnums.value = selectedPlaceWeatherEnums.value
    }else{
        selectedWeatherEnums.value = mainWeatherEnums.value
    }

    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        appBar = {
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 35.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    text = "Favourite Places",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

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
        },
        peekHeight = 75.dp,
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
                baseLogger("Width ---", width * 0.75)

                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                ) {
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
                                style = TextStyle(
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
                                text = "Maps",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    if(listView.value)
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

                    else
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ){
                            MapView(
                                screensViewModel.listSavedPlaces.value
                            ){
                                sheetState.value = PlacesSheetEnums.VIEW
                                screensViewModel.selectedPlace.value = it
                                openSheet.invoke()
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
                                    showMap = backdropScaffoldState.currentValue == BackdropValue.Concealed
                                )
                            }
                            PlacesSheetEnums.VIEW -> {
                                LocationView(
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
}

@Composable
fun MapView(
    list: List<SavedPlace>,
    openSheet: (SavedPlace) -> Unit
){

    GoogleMap(
        modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(10.dp)),
        cameraPositionState = rememberCameraPositionState(key = Random.toString())  {
            position = CameraPosition.fromLatLngZoom(list[0].location, 13f)
        }
    ) {
        for (marker in list){
            Marker(
                position = marker.location,
                title = marker.name,
                onInfoWindowClick = {
                    openSheet.invoke(marker)
                }
            )
        }
    }
}

@Composable
fun ListView(
    list: List<SavedPlace>,
    openSheet: (SavedPlace) -> Unit
){
    Column {
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
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 18.sp,
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
                    text = placeItem.date,
                    style = TextStyle(
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
    showMap: Boolean = false,
    placeItem: SavedPlace
){
    val screensViewModel = hiltViewModel<ScreensViewModel>()
    val weatherRequest = remember { mutableStateOf(WeatherRequest()) }
    val hasWeatherValues = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedLocal = {
        scope.launch {
            screensViewModel.getWeatherInfo(
                lat = placeItem.location.latitude.toFloat(),
                lon = placeItem.location.longitude.toFloat(),
                context = context
            )
            screensViewModel.currentWeather.collect {
                val value = it.data
                if(value != null){
                    weatherRequest.value = value
                    selectedPlaceWeatherEnums.value = getWeatherEnum(value.current.weather[0].id)
                    hasWeatherValues.value = true
                }
            }
        }
    }
    selectedLocal.invoke()

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
                        style = TextStyle(
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
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 30.sp
                    )
                )

                Text(
                    text = "at ${placeItem.name}, ${placeItem.locality}",
                    style = TextStyle(
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
                            position = placeItem.location,
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

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
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
                                style = TextStyle(
                                    color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center
                            )
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
}

@Composable
fun AddLocation(
    showMap: Boolean = false
){

    val markerPosition = remember { mutableStateOf(LatLng(-1.2855328,36.7972995)) }
    val scope = rememberCoroutineScope()
    val fetchingMessage = remember { mutableStateOf("Getting the place details") }
    val isPlaceSelected = remember { mutableStateOf(false) }
    val isFetchingDetails = remember { mutableStateOf(true) }
    val selectedPlace = remember { mutableStateOf(SavedPlace()) }
    val screensViewModel = hiltViewModel<ScreensViewModel>()
    val context = LocalContext.current

    val weatherRequest = remember { mutableStateOf(WeatherRequest()) }

    if(!isPlaceSelected.value){
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
                selectedPlace.value = selectedPlace.value.copy(location = LatLng(it.location.latitude, it.location.longitude))
                markerPosition.value = LatLng(selectedPlace.value.location.latitude,selectedPlace.value.location.longitude)
                requestWeatherDetails.invoke()
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ){

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

        if(screensViewModel.isRequesting.value){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = Color.White,
            )
        }

        if(isPlaceSelected.value){
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
                            style = TextStyle(
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
                        if(showMap)
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(markerPosition.value, 15f)
                                }
                            ) {
                                Marker(
                                    position = markerPosition.value,
                                    title = selectedPlace.value.name,
                                )
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

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
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
                            style = TextStyle(
                                color = backgroundColorExt(selectedPlaceWeatherEnums.value),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))

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
                            isPlaceSelected.value = true
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
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            )
        )
    }
}