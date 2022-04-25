package com.example.yelpsearchapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yelpsearchapplication.models.Business
import com.example.yelpsearchapplication.repositories.BusinessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val businessRepository: BusinessRepository
    ): ViewModel() {
    val austinRestaurantsList = MutableLiveData<List<Business>>()

    val error = MutableLiveData<String>()

    val searchEditTextLiveData = MutableLiveData<String>()

    fun getAustinRestaurantsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = businessRepository.getAustinRestaurantsList()
                if (response.isSuccessful) {
                    response.body()?.let { businessSearchResponse ->
                        austinRestaurantsList.postValue(businessSearchResponse.businesses)
                    }
                } else {
                    error.postValue("Something went wrong, please try again.")
                }
            } catch (e: Exception) {
                error.postValue(e.message.toString())
            }
        }
    }

    fun updateRestaurantsList() {
        val updateList = ArrayList<Business>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = businessRepository.getAustinRestaurantsList()
                if (response.isSuccessful) {
                    response.body()?.let { businessSearchResponse ->
                        for (business in businessSearchResponse.businesses) {
                            if (business.name.contains(searchEditTextLiveData.value.toString())) {
                                updateList.add(business)
                            }
                        }
                        austinRestaurantsList.postValue(updateList)
                    }
                } else {
                    error.postValue("Something went wrong, please try again.")
                }
            } catch (e: Exception) {
                error.postValue(e.message.toString())
            }
        }
    }
}