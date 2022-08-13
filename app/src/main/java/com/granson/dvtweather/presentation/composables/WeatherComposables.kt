package com.granson.dvtweather.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.granson.dvtweather.ui.theme.DVTColors
import com.granson.dvtweather.ui.theme.DVTWeatherTheme
import com.granson.dvtweather.R
import com.granson.dvtweather.enums.WeatherEnums
import com.granson.dvtweather.ui.theme.Typography

@Composable
fun BackImage(backImage: Int, temp: String, weather: WeatherEnums, location: String){
    Box(
        contentAlignment = Alignment.TopCenter
    ){
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(backImage),
            contentDescription = "Back Image",
            alignment = Alignment.TopStart,
            contentScale = ContentScale.FillWidth
        )
        CurrentWeatherCol(
            65.dp,
            temp,
            weather,
            location
        )
    }
}


@Composable
fun CurrentWeatherCol(
    topHeight: Dp,
    temp: String,
    weather: WeatherEnums,
    location: String
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = topHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier.width(100.dp)
        ){
            Image(
                modifier = Modifier.size(20.dp).align(alignment = Alignment.TopEnd),
                painter = painterResource(R.drawable.ic_outline_circle_42),
                contentDescription = "degrees"
            )

            Text(
                modifier = Modifier.align(alignment = Alignment.BottomCenter).padding(top = 10.dp),
                text = temp,
                style = Typography.body1.copy(
                    color = Color.White,
                    fontSize = 55.sp
                )
            )
        }

        Text(
            text = weather.name,
            style = Typography.body1.copy(
                color = Color.White,
                fontSize = 45.sp
            )
        )

        Text(
            text = "at $location",
            style = Typography.body1.copy(
                color = Color.White,
                fontSize = 17.sp
            )
        )

    }
}

@Composable
fun TempDisplay(temp: String, description: String){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.width(35.dp)
        ){
            Image(
                modifier = Modifier.size(8.dp).align(alignment = Alignment.TopEnd),
                painter = painterResource(R.drawable.ic_outline_circle_42),
                contentDescription = "degrees"
            )

            Text(
                modifier = Modifier.align(alignment = Alignment.BottomCenter).padding(top = 3.dp),
                text = temp,
                style = Typography.body1.copy(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Text(
            text = description,
            style = Typography.body1.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun DateDisplay(date: String, weather: WeatherEnums, temp: String, ){
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = date,
            style = Typography.body1.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ){
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(weatherIcon(weather)),
                contentDescription = "Weather"
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ){
            Image(
                modifier = Modifier.size(8.dp).align(alignment = Alignment.TopEnd),
                painter = painterResource(R.drawable.ic_outline_circle_42),
                contentDescription = "degrees"
            )

            Text(
                modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(top = 3.dp, end = 8.dp),
                text = temp,
                style = Typography.body1.copy(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

fun weatherIcon(weatherState: WeatherEnums): Int {
    return when(weatherState){
        WeatherEnums.SUNNY -> R.drawable.clear
        WeatherEnums.CLOUDY -> R.drawable.partlysunny
        WeatherEnums.RAINY -> R.drawable.rain
    }
}

fun imageToDisplay(weatherState: WeatherEnums): Int {
    return when(weatherState){
        WeatherEnums.SUNNY -> R.drawable.forest_sunny
        WeatherEnums.CLOUDY -> R.drawable.forest_cloudy
        WeatherEnums.RAINY -> R.drawable.forest_rainy
    }
}

fun backgroundColor(weatherState: WeatherEnums): Color {
    return when(weatherState){
        WeatherEnums.SUNNY -> DVTColors.DVTGreen
        WeatherEnums.CLOUDY -> DVTColors.DVTGray
        WeatherEnums.RAINY -> DVTColors.DVTBlack
    }
}

fun backgroundColorExt(weatherState: WeatherEnums): Color {
    return when(weatherState){
        WeatherEnums.SUNNY -> DVTColors.DVTYellowBack
        WeatherEnums.CLOUDY -> DVTColors.DVTBlackBack
        WeatherEnums.RAINY -> DVTColors.DVTGrayBack
    }
}

@Composable
fun DVTEditText(
    modifier: Modifier = Modifier,
    value: String = "",
    fieldPlaceholder: String = "",
    onValueChanged: (String) -> Unit = {}
) {
    var textState by remember { mutableStateOf(value) }
    val isInactiveState = remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current


    TextField(
        textStyle = Typography.body1.copy(
            fontSize = 14.sp,
            color =  Color.Gray
        ),
        modifier = modifier
            .height(56.dp)
            .onFocusChanged { isInactiveState.value = !it.isFocused },
        value = textState,
        onValueChange = {
            textState = it
            onValueChanged(it)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        placeholder = {
            if (fieldPlaceholder.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(),
                    text = fieldPlaceholder.ifEmpty { "" },
                    style = Typography.body1.copy(
                        fontSize = 14.sp,
                        color =  Color.LightGray
                    ),
                    color = Color.LightGray
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(10.dp),
        visualTransformation = VisualTransformation.None,
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DVTWeatherTheme {

    }
}