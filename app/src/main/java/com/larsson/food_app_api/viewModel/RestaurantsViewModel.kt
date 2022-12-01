package com.larsson.food_app_api.viewModel

import androidx.compose.runtime.*
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
    private var restaurantFilterIds = mutableListOf<String>()
    private var filtersFromApi: Filter? by mutableStateOf(null)
    var filters = mutableStateListOf<Filter?>()
    var detailsVisable: Boolean by mutableStateOf(false)
    var currentRestaurant: Restaurant? by  mutableStateOf(null)
   // var activeFilter: Boolean by mutableStateOf(false)
    var visibleRestaurants = mutableStateListOf<Restaurant>() // this should always be the last step
    private var activeFilterIds = mutableStateListOf<String>() //filterIds




   /* fun deselectFilter(currentFilter: Filter) {
        val filterId = currentFilter.id
        activeFilterIds.remove(filterId)


    }*/

    fun getFiltredRestaurants(currentFilter: Filter, didDeselect: Boolean) {

        // Current filterId : String
        val filterId = currentFilter.id

        // Remove all restaurants from list
        visibleRestaurants.clear()

        if (!didDeselect) {
            // add filterId to List of active filters
            activeFilterIds.add(filterId)
        }
        if (didDeselect) {
            // Remove selected filter from list of active filters
              activeFilterIds.remove(filterId)
        }


        // List of all restaurants : List<Restaurant>
        val restaurants = restaurantsResponse?.restaurants


        if (restaurants != null) {
            // Loop through all restaurants and add only the ones that meet the specified conditions
            for (restaurant in restaurants) {
                    val matchingFilter = restaurant.filterIds.containsAll(activeFilterIds)
                    if (matchingFilter) {

                        visibleRestaurants.add(restaurant)
                    }
            }
        }

        println("matchingFIlterIds: ${visibleRestaurants.toList()}")

    }

    // Get filter-objects that are fetched from dynamic API endpoint
    fun createFilters() {

        val filter = Filter(filtersFromApi?.id!!, filtersFromApi!!.imageUrl, filtersFromApi!!.name)
        filters.add(filter)

        return
    }

    private suspend fun addFiltersToList() {

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
                visibleRestaurants.addAll(restaurantsList.restaurants)

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

