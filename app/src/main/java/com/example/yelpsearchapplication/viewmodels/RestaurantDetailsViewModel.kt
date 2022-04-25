package com.example.yelpsearchapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yelpsearchapplication.models.Business
import com.example.yelpsearchapplication.networks.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantDetailsViewModel: ViewModel() {
    val restaurantDetailsLiveData = MutableLiveData<Business>()

    val error = MutableLiveData<String>()

    private val retrofitClient = RetrofitClient.getRetrofitService()

    fun getRestaurantDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = retrofitClient.getRestaurantDetail(id)
                if (response.isSuccessful) {
                    response.body()?.let { business ->
                        restaurantDetailsLiveData.postValue(business)
                    }
                } else {
                    error.postValue("Something went wrong, please retry.")
                }
            } catch (e: Exception) {
                error.postValue(e.message)
            }
        }
    }
}