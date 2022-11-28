package com.larsson.food_app_api.model


import com.google.gson.annotations.SerializedName

data class Restaurants(
    @SerializedName("restaurants")
    val restaurants: List<Restaurant>
)