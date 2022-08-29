package com.granson.dvtweather

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.granson.dvtweather.data.models.LocationDetails
import com.granson.dvtweather.enums.WeatherEnums
import com.granson.dvtweather.utils.Common
import com.granson.dvtweather.utils.Common.CLEAR_IDS
import com.granson.dvtweather.utils.Common.CLOUDY_IDS
import com.granson.dvtweather.utils.Common.DRIZZLE_IDS
import com.granson.dvtweather.utils.Common.SNOW_IDS
import com.granson.dvtweather.utils.Common.STORM_IDS
import com.granson.dvtweather.utils.Common.mainWeatherEnums
import com.granson.dvtweather.utils.Common.selectedPlaceWeatherEnums
import com.granson.dvtweather.utils.Common.selectedWeatherEnums
import com.granson.dvtweather.utils.Common.userCurrentLocation
import com.granson.dvtweather.utils.Common.userLocationDetails
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import java.text.SimpleDateFormat
import java.util.*

internal class CommonTest {

    @Test
    fun getSelectedWeatherEnums() {
        assertThat(selectedWeatherEnums.value).isEqualTo( WeatherEnums.SUNNY)
        selectedWeatherEnums.value = WeatherEnums.RAINY
        assertThat(selectedWeatherEnums.value).isEqualTo( WeatherEnums.RAINY)
    }


    @Test
    fun getMainWeatherEnums() {
        assertThat(mainWeatherEnums.value).isEqualTo( WeatherEnums.SUNNY)
        // Set Main WeatherEnums
        mainWeatherEnums.value = WeatherEnums.RAINY
        assertThat(mainWeatherEnums.value).isEqualTo( WeatherEnums.RAINY)
    }


    @Test
    fun getUserCurrentLocation() {
        // Initial setup
        assertThat(userCurrentLocation).isEqualTo(LatLng(-1.23456, 36.2345))
        userCurrentLocation = LatLng(-1.0000, 36.2345)
        assertThat(userCurrentLocation).isEqualTo(LatLng(-1.0000, 36.2345))
    }


    @Test
    fun getUserLocationDetails() {
        // Initial setup
        assertThat(userLocationDetails.value).isEqualTo(LocationDetails())
    }

    @Test
    fun getSelectedPlaceWeatherEnums() {
        assertThat(selectedPlaceWeatherEnums.value).isEqualTo( selectedWeatherEnums.value)
    }

    @Test
    fun assert_weather_id_lists() {
        assertThat(STORM_IDS.size).isEqualTo(1)
        assertThat(CLOUDY_IDS.contains(802)).isTrue()
        assertThat(DRIZZLE_IDS.contains(302)).isTrue()
        assertThat(DRIZZLE_IDS.contains(802)).isFalse()
        assertThat(CLEAR_IDS.contains(202)).isTrue()
        assertThat(SNOW_IDS.contains(602)).isTrue()
    }


    @Test
    fun getWeatherEnum() {
        assertThat(Common.getWeatherEnum(202)).isEqualTo(WeatherEnums.SUNNY)
        assertThat(Common.getWeatherEnum(803)).isEqualTo(WeatherEnums.CLOUDY)
        assertThat(Common.getWeatherEnum(302)).isEqualTo(WeatherEnums.RAINY)
        assertThat(Common.getWeatherEnum(502)).isEqualTo(WeatherEnums.RAINY)
        assertThat(Common.getWeatherEnum(602)).isEqualTo(WeatherEnums.RAINY)
    }

    @Test
    fun daysFlow() {
        assertThat(Common.daysFlow().size).isEqualTo(8)
    }

    @Test
    fun getGetCurrentDate() {
        val sdf = SimpleDateFormat("d MMM, yyyy")
        assertThat( Common.getCurrentDate.invoke()).isEqualTo(sdf.format(Date()))
    }
}