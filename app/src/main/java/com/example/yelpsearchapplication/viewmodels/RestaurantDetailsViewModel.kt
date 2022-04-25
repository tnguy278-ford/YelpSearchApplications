package com.example.yelpsearchapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yelpsearchapplication.models.Business
import com.example.yelpsearchapplication.repositories.BusinessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantDetailsViewModel(
    private var businessRepository: BusinessRepository
): ViewModel() {
    val restaurantDetailsLiveData = MutableLiveData<Business>()

    val error = MutableLiveData<String>()

    fun getRestaurantDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = businessRepository.getRestaurantDetails(id)
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