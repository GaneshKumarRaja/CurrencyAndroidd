package com.exp.currencyapp.di.module

import androidx.lifecycle.ViewModel
import com.exp.currencyapp.di.ViewModelKey
import com.exp.currencyapp.view_model.RatestViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class RatesViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RatestViewModel::class)
    abstract fun bindMyViewModel(myViewModel: RatestViewModel): ViewModel
}