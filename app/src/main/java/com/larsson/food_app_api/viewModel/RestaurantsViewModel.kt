package com.larsson.food_app_api.viewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larsson.food_app_api.model.Filter
import com.larsson.food_app_api.model.Restaurant
import com.larsson.food_app_api.model.Restaurants
import com.larsson.food_app_api.network.ApiService
import kotlinx.coroutines.launch


class RestaurantsViewModel: ViewModel() {
    var restaurantsResponse: Restaurants? by mutableStateOf(null)
    private var errorMessage: String by mutableStateOf("")
    var restaurantFilterIds = mutableListOf<String>()
    var filtersFromApi: Filter? by mutableStateOf(null)
    var filters = mutableStateListOf<Filter?>()



    // Get filter-objects that are fetched from dynamic API endpoint
    private fun createFilters(): Filter? {

        // println("filtersfromapi inside create filters $filtersFromApi")

        val filter = Filter(filtersFromApi?.id!!, filtersFromApi!!.imageUrl, filtersFromApi!!.name)
        println("Local filter: inside Create filter$filter")
        filters.add(filter)
        println("Filters from create filters: $filters")


        return filtersFromApi
    }

    private suspend fun addFiltersToList() {
        // viewModelScope.launch {
        println("Inside addFiltersToList: $restaurantFilterIds")

        for (restaurantId in restaurantFilterIds) {
            getFiltersFromApi(restaurantId)
            createFilters()

        }

    }


    // Get Filters that are attatched to each Restaurant
    private fun getFilterIds() {
        if (restaurantsResponse != null) {
            val restaurants = restaurantsResponse!!.restaurants


            // Add all id's to array
            for (restaurant in restaurants) {
                if (restaurant.filterIds.isNotEmpty()) {

                    // Loop through all id's in selected restaurant
                    for ((id)in restaurant.filterIds.withIndex()) {

                        // Add to filter-array if id doesn't already exist in said array.
                        if (!restaurantFilterIds.contains(restaurant.filterIds[id]))
                        {

                            restaurantFilterIds.add(restaurant.filterIds[id])
                        }
                    }

                }

            }

        }
    }



    private suspend fun getFiltersFromApi(id: String) {

        val apiService = ApiService.getInstance()

        try {
            val filtersList = apiService.getFilters(id)
            filtersFromApi = filtersList
            // println("filts from api $filtersFromApi")
        } catch (e:java.lang.Exception) {
            errorMessage = e.message.toString()
        }




        return
    }

    fun getRestaurants(id: String?) {
        println(id)
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                val restaurantsList = apiService.getRestaurants()
                restaurantsResponse = restaurantsList

                getFilterIds()
                addFiltersToList()

            } catch (e:java.lang.Exception) {
                errorMessage = e.message.toString()
            }
            println("api filterFromApi $filtersFromApi")
            println("api filter ${filters[0]}")
        }

    }

}

