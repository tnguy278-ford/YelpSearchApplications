package com.example.yelpsearchapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.yelpsearchapplication.models.Business
import com.example.yelpsearchapplication.networks.RetrofitClient
import com.example.yelpsearchapplication.networks.RetrofitService
import com.example.yelpsearchapplication.viewmodels.RestaurantDetailsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RestaurantDetailsViewModelTest {
    @Mock
    lateinit var retrofitService: RetrofitService

    @Mock
    lateinit var businessObserver: Observer<Business>

    @Mock
    lateinit var errorObserver: Observer<String>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        retrofitService = RetrofitClient.getRetrofitService()
    }

    @Test
    fun `getRestaurantDetails success testing`() {
        runBlockingTest {

            val dummyResponse = Response.success(Gson().fromJson(Constant.BUSINESS_DETAILS_RESPONSE, Business::class.java))

            doReturn(dummyResponse).`when`(retrofitService).getRestaurantDetail(TEST_ID)

            val viewModel = RestaurantDetailsViewModel()

            viewModel.restaurantDetailsLiveData.observeForever(businessObserver)

            viewModel.getRestaurantDetails(TEST_ID)

            verify(retrofitService).getRestaurantDetail(TEST_ID)

            val expectedResult = Gson().fromJson(Constant.BUSINESS_DETAILS_RESPONSE, Business::class.java)

            verify(businessObserver).onChanged(expectedResult)

            viewModel.restaurantDetailsLiveData.removeObserver(businessObserver)
        }
    }

    @Test
    fun `getRestaurantDetails returns no data for categories testing`() {
        runBlockingTest {

            val dummyResponse = Response.success(Gson().fromJson(Constant.BUSINESS_DETAILS_EMPTY_RESPONSE, Business::class.java))

            doReturn(dummyResponse).`when`(retrofitService).getRestaurantDetail(TEST_ID)

            val viewModel = RestaurantDetailsViewModel()

            viewModel.error.observeForever(errorObserver)

            viewModel.getRestaurantDetails(TEST_ID)

            verify(retrofitService).getRestaurantDetail(TEST_ID)

            verify(errorObserver).onChanged("Failed to load Restaurant data")

            viewModel.error.removeObserver(errorObserver)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `getRestaurantDetails exception testing`() = runBlockingTest {

        val exception = RuntimeException("Internet is not available.")

        doThrow(exception).`when`(retrofitService.getRestaurantDetail(TEST_ID))

        val viewModel = RestaurantDetailsViewModel()

        viewModel.error.observeForever(errorObserver)

        viewModel.getRestaurantDetails(TEST_ID)

        verify(retrofitService).getRestaurantDetail(TEST_ID)

        val expectedResult = "Error is : $exception"
        verify(errorObserver).onChanged(expectedResult)

        viewModel.error.removeObserver(errorObserver)
    }

    companion object {
        const val TEST_ID = "MGzro82Fi4LYvc86acoONQ"
    }
}