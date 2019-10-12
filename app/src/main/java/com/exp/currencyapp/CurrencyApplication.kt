package com.exp.currencyapp

import android.app.Application
import com.exp.currencyapp.di.component.AppComponent
import com.exp.currencyapp.di.component.DaggerAppComponent

class CurrencyApplication : Application() {


    override fun onCreate() {

        super.onCreate()
    }

    fun initComponet(): AppComponent {

        return DaggerAppComponent.create()

    }


}