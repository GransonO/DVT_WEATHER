package com.granson.dvtweather.presentation.composables.screens.viewModels

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.common.truth.Truth.assertThat
import com.granson.dvtweather.presentation.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var context: Context

    @Before
    fun setUp(){
        fusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)
        context = mock(Context::class.java)
        mainViewModel = MainViewModel(fusedLocationProviderClient, context)
    }

    @Test
    fun getCurrentLocation() {
        runBlocking {
            mainViewModel.getCurrentLocation().collect{
                assertThat(it).isEqualTo(null)
            }
        }
    }

    @Test
    fun getLocationEnabled() {
    }
}