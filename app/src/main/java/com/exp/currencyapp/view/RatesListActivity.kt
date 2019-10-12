package com.exp.currencyapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exp.currencyapp.CurrencyApplication
import com.exp.currencyapp.R

import com.exp.currencyapp.adapter.RatesAdapter
import com.exp.currencyapp.databinding.ActivityMainBinding
import com.exp.currencyapp.di.DaggerViewModelFactory
import com.exp.currencyapp.model.RateModel
import com.exp.currencyapp.view_model.RatestViewModel


import javax.inject.Inject
import kotlinx.coroutines.*;


class RatesListActivity : BaseActivity(), RatesAdapter.OnRateInteraction {

    var binding: ActivityMainBinding? = null

    private var CanUpdateRates = false;

    private val intervel: Long = 1000

    @Inject
    lateinit var ratesAdapter: RatesAdapter

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory

    @Inject
    lateinit var viewModel: RatestViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        configAdapter()
        setViewModelobserver()

    }

    fun configAdapter() {

        binding!!.rcrRates.adapter = ratesAdapter
        // set callback
        ratesAdapter.setDelegate(this)

        val mLayoutManager: RecyclerView.LayoutManager
        mLayoutManager = LinearLayoutManager(this)
        binding!!.rcrRates.layoutManager = mLayoutManager

        val mDividerItemDecoration = DividerItemDecoration(
            binding!!.rcrRates.getContext(),
            mLayoutManager.orientation
        )

        binding!!.rcrRates.addItemDecoration(mDividerItemDecoration)
    }


    override fun initDI() {

        (application as CurrencyApplication).initComponet().inject(this)

    }


    override fun onResume() {

        super.onResume()

        if (!isNetworkConnected()) {
            Toast.makeText(this, "No Intent Connection", Toast.LENGTH_SHORT).show()
        }

        CanUpdateRates = true

        GlobalScope.launch(Dispatchers.Main) {
            refreshThread()
        }

    }


    suspend fun refreshThread() = withContext(Dispatchers.Main) {

        while (CanUpdateRates) {
            delay(intervel)
            viewModel.refereshRates()
        }

    }


    fun setValueToRecyclerView(data: List<RateModel>) {

        if (data != null) {

            GlobalScope.launch(Dispatchers.Main) {
                ratesAdapter.updateList(data)
            }

        }

    }


    fun setViewModelobserver() {

        viewModel.getRatesLiveData().observe(this, Observer {
            setValueToRecyclerView(it)
        })

    }


    // call back methods of rates adapter
    override fun scrollToTop() {

        binding!!.rcrRates.scrollToPosition(0)

    }

    override fun onRateChanged(currencyName: String, value: Float) {

        viewModel?.setNewBase(currencyName, value)

    }

    override fun onValueChanged(value: Float) {

        viewModel?.setNewBaseValue(value)

    }


}
