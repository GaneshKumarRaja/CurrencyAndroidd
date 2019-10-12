package com.exp.currencyapp.di.component

import com.exp.currencyapp.di.module.NetworkModule
import com.exp.currencyapp.di.module.RatesViewModelModule
import com.exp.currencyapp.http.RateService
import com.exp.currencyapp.view.RatesListActivity
import com.exp.currencyapp.view_model.RatestViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RatesViewModelModule::class])
interface AppComponent {
    fun inject(activity: RatestViewModel)
    fun inject(service: RateService)
    fun inject(service: RatesListActivity)
}