package com.exp.currencyapp.http

import com.exp.currencyapp.model.RateResponseModel
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface HttpInterface {

    @GET("latest")
    fun getLatestRates(@Query("base") baseRate: String): Single<RateResponseModel>

}