/*
package com.larsson.food_app_api.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larsson.food_app_api.model.Restaurant
import com.larsson.food_app_api.network.ApiService
import kotlinx.coroutines.launch

class RestaurantViewModel: ViewModel() {
    var restaurantListResponse: List<Restaurant> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")

    fun getRestaurantList() {
        viewModelScope.launch {
            var apiService = ApiService.getInstance()
            try {
                val restaurantList = apiService.getRestaurants()
                restaurantListResponse = restaurantList
            } catch (e:java.lang.Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

}*/
