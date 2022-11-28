package com.larsson.food_app_api.model


import com.google.gson.annotations.SerializedName

data class Filter(
    @SerializedName("id")
    val id: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("name")
    val name: String
)