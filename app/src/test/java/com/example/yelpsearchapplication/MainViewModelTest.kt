package com.example.yelpsearchapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.yelpsearchapplication.models.Business
import com.example.yelpsearchapplication.models.BusinessSearchResponse
import com.example.yelpsearchapplication.networks.RetrofitClient
import com.example.yelpsearchapplication.networks.RetrofitService
import com.example.yelpsearchapplication.viewmodels.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @Mock
    lateinit var retrofitService: RetrofitService

    @Mock
    lateinit var businessListObserver: Observer<List<Business>>

    @Mock
    lateinit var errorObserver: Observer<String>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(dispatcher)

    @Before
    fun setup(){
        retrofitService = RetrofitClient.getRetrofitService()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `getAustinRestaurantList success testing`() {
        runBlocking {

            val dummyResponse = Response.success(Gson().fromJson(Constant.BUSINESS_LIST_RESPONSE, BusinessSearchResponse::class.java))

            Mockito.`when`(retrofitService.getAustinRestaurantsList()).thenReturn(dummyResponse)

            val viewModel = MainViewModel()

            viewModel.austinRestaurantsList.observeForever(businessListObserver)

            viewModel.getAustinRestaurantsList()

            verify(retrofitService).getAustinRestaurantsList()

            val expectedResult = Gson().fromJson(Constant.BUSINESS_LIST_RESPONSE, BusinessSearchResponse::class.java)

            verify(businessListObserver).onChanged(expectedResult.businesses)

            viewModel.austinRestaurantsList.removeObserver(businessListObserver)
        }
    }

    @Test
    fun `getAustinRestaurantsList returns no data testing`() {
        runBlocking {

            val dummyResponse = Response.success(Gson().fromJson(Constant.BUSINESS_LIST_EMPTY_RESPONSE, BusinessSearchResponse::class.java))

            Mockito.`when`(retrofitService.getAustinRestaurantsList()).thenReturn(dummyResponse)

            val viewModel = MainViewModel()

            viewModel.error.observeForever(errorObserver)

            viewModel.getAustinRestaurantsList()

            verify(retrofitService).getAustinRestaurantsList()

            verify(errorObserver).onChanged("Failed to load Restaurant data")

            viewModel.error.removeObserver(errorObserver)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `getAustinRestaurantsList exception testing`() = runBlockingTest {

        val exception = RuntimeException("Internet is not available.")

        Mockito.`when`(retrofitService.getAustinRestaurantsList()).doThrow(exception)

        val viewModel = MainViewModel()

        viewModel.error.observeForever(errorObserver)

        viewModel.getAustinRestaurantsList()

        verify(retrofitService).getAustinRestaurantsList()

        val expectedResult = "Error is : $exception"
        verify(errorObserver).onChanged(expectedResult)

        viewModel.error.removeObserver(errorObserver)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }
}