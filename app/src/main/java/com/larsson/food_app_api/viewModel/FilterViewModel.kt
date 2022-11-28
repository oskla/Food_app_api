package com.larsson.food_app_api.viewModel

import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larsson.food_app_api.model.Filter

import kotlinx.coroutines.launch



/*
    class FilterViewModel: ViewModel() {

        var filterResponse: Filter? by mutableStateOf(null)
        var errorMessage: String by mutableStateOf("")

        fun getFilters() {
            viewModelScope.launch {
                var apiService = ApiService2.getInstance()
                try {
                    val filterList = apiService.getFilters()
                    filterResponse = filterList
                } catch (e:java.lang.Exception) {
                    errorMessage = e.message.toString()
                }
            }
        }

    }
*/
