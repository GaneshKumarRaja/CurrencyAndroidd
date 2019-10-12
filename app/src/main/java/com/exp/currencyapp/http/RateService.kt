package com.exp.currencyapp.http


import com.exp.currencyapp.di.component.DaggerAppComponent
import com.exp.currencyapp.model.RateResponseModel
import io.reactivex.Single
import javax.inject.Inject

class RateService @Inject public constructor() {

    @Inject
    lateinit var api: HttpInterface

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getCountries(s: String): Single<RateResponseModel> {
        return api.getLatestRates(s)
    }

}