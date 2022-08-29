package com.granson.dvtweather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.common.truth.Truth.assertThat
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.data.repository.repos.DataRepository
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import com.granson.dvtweather.fake.FakeDataRepository
import com.granson.dvtweather.fake.FakePlaceRepository
import com.granson.dvtweather.fake.FakeWeatherRepository
import com.granson.dvtweather.presentation.composables.screens.viewModels.ScreensViewModel
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.PlaceLocation
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ScreensViewModelTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var dataRepository: DataRepository

    lateinit var screensViewModel: ScreensViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var context: Context

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val lock = CountDownLatch(1) // Not 100% on this, will need more reading

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        connectivityManager = mock(ConnectivityManager::class.java)
        weatherRepository = FakeWeatherRepository()
        placeRepository = FakePlaceRepository()
        dataRepository = FakeDataRepository()

        screensViewModel = ScreensViewModel(
            weatherRepository = weatherRepository,
            placeRepository = placeRepository,
            dataRepository = dataRepository
        )
    }

    @Test
    fun test_getInternetStatus() {
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        val activeNetworkInfo = mock(NetworkInfo::class.java)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(activeNetworkInfo)

        Mockito.`when`(activeNetworkInfo.isConnectedOrConnecting).thenReturn(true)
        assertThat(screensViewModel.getInternetStatus(context)).isTrue()

        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(null)
        assertThat(screensViewModel.getInternetStatus(context)).isFalse()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_placeSearch() {
        runTest {
            screensViewModel.placeSearch("Test", "testKey")
            lock.await(1000, TimeUnit.MILLISECONDS)

            assertThat(screensViewModel.queryPlaces.value.predictions.size).isEqualTo(3)

            screensViewModel.placeSearch("Nairobi", "testKey")
            assertThat(screensViewModel.queryPlaces.value.predictions.size).isEqualTo(0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getPlaceDetails() {
        runTest {
            screensViewModel.getPlaceDetails("testPlaceId1", "testKey")
            lock.await(1000, TimeUnit.MILLISECONDS)

            val item = screensViewModel.placeDetails.first()

            assertThat(item.name).isEqualTo("Test Place 1")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getSavedPlaces() {
        runTest {
            screensViewModel.getSavedPlaces()
            lock.await(1000, TimeUnit.MILLISECONDS)

            assertThat(screensViewModel.listSavedPlaces.value.size).isEqualTo(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_savePlace() {
        val testPlace = SavedPlace(
            location = PlaceLocation(-2.09383, 37.89887),
            name = "DB Test Place 2",
            placeId = "EWRTYUIGHJKL"
        )

        runTest {
            screensViewModel.savePlace(testPlace)
            lock.await(1000, TimeUnit.MILLISECONDS)

            assertThat(screensViewModel.isAddedSuccess.value).isTrue()
            assertThat(screensViewModel.listSavedPlaces.value.size).isEqualTo(2)
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_updatePlace() {
        val testPlace = SavedPlace(
            location = PlaceLocation(-2.09383, 37.89887),
            name = "Updated Name", // "DB Test Place 1"
            placeId = "123tester"
        )

        runTest {
            screensViewModel.updatePlace(testPlace)
            lock.await(1000, TimeUnit.MILLISECONDS)

            assertThat(screensViewModel.isUpdated.value).isTrue()
            assertThat(screensViewModel.listSavedPlaces.value.size).isEqualTo(1)
            assertThat(screensViewModel.listSavedPlaces.value).contains(testPlace)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_deletePlace() {
        val testPlace = SavedPlace(
            location = PlaceLocation(-2.09383, 37.89887),
            name = "DB Test Place 1",
            placeId = "123tester"
        )

        runTest {
            screensViewModel.deletePlace(testPlace)
            lock.await(1000, TimeUnit.MILLISECONDS)

            assertThat(screensViewModel.isDeleted.value).isTrue()
            assertThat(screensViewModel.listSavedPlaces.value.size).isEqualTo(0)
        }
    }
}

class ScreensViewModelTestModule {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var dataRepository: DataRepository

    lateinit var screensViewModel: ScreensViewModel

    @get:Rule
    val mainDispatcherRule = StandardDispatcherRule()

    @Before
    fun setUp() {
        weatherRepository = FakeWeatherRepository()
        placeRepository = FakePlaceRepository()
        dataRepository = FakeDataRepository()

        screensViewModel = ScreensViewModel(
            weatherRepository = weatherRepository,
            placeRepository = placeRepository,
            dataRepository = dataRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getWeatherInfo() {
        // Timing out test, to review further
        runTest {
            screensViewModel.getWeatherInfo(
                lat = 0.0f,
                lon= 0.0f,
                key = "123test"
            )

            val firstResult = screensViewModel.currentWeather.first()
            assertThat(firstResult.data).isEqualTo(WeatherRequest())
        }
    }



    // Reusable JUnit4 TestRule to override the Main dispatcher
    class StandardDispatcherRule @OptIn(ExperimentalCoroutinesApi::class) constructor(
        private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
    ) : TestWatcher() {
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}