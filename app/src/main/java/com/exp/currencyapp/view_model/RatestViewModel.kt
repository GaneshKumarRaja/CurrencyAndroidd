package com.exp.currencyapp.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exp.currencyapp.http.RateService
import com.exp.currencyapp.model.RateModel
import com.exp.currencyapp.model.RateResponseModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


//import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

open class RatestViewModel @Inject public constructor() : ViewModel() {

    // Default values
    private var baseCurrency: String = "EUR"

    private var baseValue: Float = 100F

    lateinit var errorMessage: String

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var ratestUpdateRepo: RateService

    private val ratesLiveData: MutableLiveData<List<RateModel>> = MutableLiveData()

    private val ShynRateUpdate: Any = Object()

    var TAG = "Currency"


    fun refereshRates() {

        disposable.add(
            ratestUpdateRepo.getCountries(baseCurrency)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RateResponseModel>() {

                    override fun onSuccess(value: RateResponseModel?) {
                        handleResponse(value!!)
                    }

                    override fun onError(e: Throwable?) {
                        handleError(e)
                    }
                })
        )

    }

    fun handleResponse(resultData: RateResponseModel) {

        if (resultData != null)
            updateLatestRates(resultData)
    }

    fun handleError(error: Throwable?) {

        if (error != null)
            errorMessage = error!!.message!!

    }


    fun getRatesLiveData(): MutableLiveData<List<RateModel>> = ratesLiveData


    /**
     * Converts the latest data to a List<CurrencyRate>
     *
     * @param latestRates Latest Rates received from ApiClient.
     */
    private fun updateLatestRates(latestRates: RateResponseModel) {

        val newCurrencyRates: MutableList<RateModel> = mutableListOf<RateModel>()

        synchronized(ShynRateUpdate) {

            newCurrencyRates.add(RateModel(latestRates.base!!, 1.0F, baseValue))

            latestRates.rates?.forEach { (currency, rate) ->
                newCurrencyRates.add(RateModel(currency, rate, rate * baseValue))
            }

            ratesLiveData.value = newCurrencyRates
        }
    }

    fun setNewBase(newBaseCurrency: String, newBaseValue: Float) {

        // Ignore if same
        if (baseCurrency.equals(newBaseCurrency))
            return;

        baseCurrency = newBaseCurrency
        baseValue = newBaseValue
        refereshRates()
    }

    fun setNewBaseValue(value: Float) {

        // Ignore so it doesn't enter an infinite loop
        if (baseValue.equals(value))
            return

        synchronized(ShynRateUpdate) {
            baseValue = value

            val newCurrencyRates: MutableList<RateModel> = mutableListOf<RateModel>()

            ratesLiveData.value?.forEach {
                newCurrencyRates.add(RateModel(it.currency, it.rate, it.rate * baseValue))
            }

            ratesLiveData.value = newCurrencyRates
        }

    }

    override fun onCleared() {

        super.onCleared()
        disposable.clear()
    }


}