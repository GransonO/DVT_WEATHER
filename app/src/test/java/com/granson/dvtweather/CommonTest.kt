package com.granson.dvtweather

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.granson.dvtweather.data.models.LocationDetails
import com.granson.dvtweather.enums.WeatherEnums
import com.granson.dvtweather.utils.Common
import com.granson.dvtweather.utils.Common.CLEAR_IDS
import com.granson.dvtweather.utils.Common.CLOUDY_IDS
import com.granson.dvtweather.utils.Common.DRIZZLE_IDS
import com.granson.dvtweather.utils.Common.RAIN_IDS
import com.granson.dvtweather.utils.Common.SNOW_IDS
import com.granson.dvtweather.utils.Common.STORM_IDS
import com.granson.dvtweather.utils.Common.baseLogger
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
        // assert initial state of value in Singletone
        assertThat(selectedWeatherEnums.value).isEqualTo( WeatherEnums.SUNNY)
    }

    @Test
    fun setSelectedWeatherEnums() {
        selectedWeatherEnums.value = WeatherEnums.RAINY
        assertThat(selectedWeatherEnums.value).isEqualTo( WeatherEnums.RAINY)
    }

    @Test
    fun getMainWeatherEnums() {
        assertThat(mainWeatherEnums.value).isEqualTo( WeatherEnums.SUNNY)
    }

    @Test
    fun setMainWeatherEnums() {
        mainWeatherEnums.value = WeatherEnums.RAINY
        assertThat(mainWeatherEnums.value).isNotEqualTo( WeatherEnums.SUNNY)
        // After assignment
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
    fun getSTORM_IDS() {
        assertThat(STORM_IDS.size).isEqualTo(1)
    }

    @Test
    fun getCLOUDY_IDS() {
        assertThat(CLOUDY_IDS.contains(802)).isTrue()
    }

    @Test
    fun getDRIZZLE_IDS() {
        assertThat(DRIZZLE_IDS.contains(302)).isTrue()
        assertThat(DRIZZLE_IDS.contains(802)).isFalse()
    }

    @Test
    fun getRAIN_IDS() {
        assertThat(RAIN_IDS.contains(502)).isTrue()
    }

    @Test
    fun getCLEAR_IDS() {
        assertThat(CLEAR_IDS.contains(202)).isTrue()
    }

    @Test
    fun getSNOW_IDS() {
        assertThat(SNOW_IDS.contains(602)).isTrue()
    }

    @Test
    fun getWeatherEnum() {
        assertThat(Common.getWeatherEnum(202)).isEqualTo(WeatherEnums.SUNNY)
    }

    @Test
    fun daysFlow() {
        // baseLogger("The days", Common.daysFlow())
        assertThat(Common.daysFlow().size).isEqualTo(9)
    }

    @Test
    fun getGetCurrentDate() {
        val sdf = SimpleDateFormat("d MMM, yyyy")
        assertThat( Common.getCurrentDate.invoke()).isEqualTo(sdf.format(Date()))
    }
}