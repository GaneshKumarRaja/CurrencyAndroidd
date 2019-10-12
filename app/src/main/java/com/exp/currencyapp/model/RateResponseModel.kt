package com.exp.currencyapp.model

import com.google.gson.annotations.SerializedName

 data class RateResponseModel(
    @SerializedName("base") val base: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: Map<String, Float>?
)