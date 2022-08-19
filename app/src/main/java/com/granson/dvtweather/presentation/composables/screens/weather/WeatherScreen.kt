package com.granson.dvtweather.presentation.composables.screens.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.granson.dvtweather.presentation.composables.*
import com.granson.dvtweather.utils.Common
import com.granson.dvtweather.utils.Common.baseLogger
import com.granson.dvtweather.utils.Common.currentTemp
import com.granson.dvtweather.utils.Common.dailyWeather
import com.granson.dvtweather.utils.Common.getWeatherEnum
import com.granson.dvtweather.utils.Common.selectedWeatherEnums
import com.granson.dvtweather.utils.Common.userLocationDetails
import kotlin.math.roundToInt

@Composable
fun WeatherScreen() {

    val todayWeather = dailyWeather.value[0].temp
    selectedWeatherEnums.value = Common.mainWeatherEnums.value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor(selectedWeatherEnums.value)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            BackImage(
                backImage = imageToDisplay(selectedWeatherEnums.value),
                temp = currentTemp.value,
                weather = selectedWeatherEnums.value,
                location = "${userLocationDetails.value.address}, ${userLocationDetails.value.locality}"
            )

            Box{

                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){

                        TempDisplay("${todayWeather.min.roundToInt()}", "Min")

                        TempDisplay(currentTemp.value, "Current")

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
                        dailyWeather.value.forEach {
                            DateDisplay(
                                date = Common.daysOrder.value[dailyWeather.value.indexOf(it)],
                                weather = getWeatherEnum(it.weather[0].id),
                                temp = it.temp.max.roundToInt().toString()
                            )
                        }
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

