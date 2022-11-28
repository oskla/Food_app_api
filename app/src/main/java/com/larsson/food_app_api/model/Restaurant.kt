package com.larsson.food_app_api.model


import com.google.gson.annotations.SerializedName

data class Restaurant(
    @SerializedName("delivery_time_minutes")
    val deliveryTimeMinutes: Int,
    @SerializedName("filterIds")
    val filterIds: List<String>,
    @SerializedName("id")
    val id: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rating")
    val rating: Double

)

