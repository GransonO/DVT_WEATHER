package com.granson.dvtweather.presentation.composables.screens.viewModels

import android.content.Context
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.common.truth.Truth.assertThat
import com.granson.dvtweather.presentation.MainViewModel
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var context: Context
    private lateinit var locationManager: LocationManager

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp(){
        fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
        locationManager = mock(LocationManager::class.java)
        context = mock(Context::class.java)
        mainViewModel = MainViewModel(fusedLocationProviderClient, context)
    }

//    @Test
//    fun test_get_current_location() {
//        // Will address you later
//        val location = Location("testGPSProvider")
//        location.apply {
//            latitude = -2.422
//            longitude = 37.084
//            accuracy = 3.0f
//        }
//        fusedLocationProviderClient.setMockLocation(location)
//
//        runTest {
//            val result = mainViewModel.getCurrentLocation().first()
//            assertThat(result).isEqualTo(null)
//        }
//    }

    @Test
    fun test_is_location_enabled() {
        Mockito.`when`(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)
        Mockito.`when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
        Mockito.`when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true)

        assertThat(mainViewModel.locationEnabled.invoke()).isTrue()

        Mockito.`when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)
        Mockito.`when`(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false)
        // Asset that all services are required
        assertThat(mainViewModel.locationEnabled.invoke()).isFalse()
    }


}
