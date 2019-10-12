package com.exp.currencyapp.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.exp.currencyapp.R
import com.exp.currencyapp.databinding.AdapterRatesLayoutBinding
import com.exp.currencyapp.model.RateModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class RatesAdapter @Inject public constructor() :
    RecyclerView.Adapter<RateViewHolder>() {

    lateinit  var callback:OnRateInteraction

    private val valueWatcher: TextWatcher
    private var ratesList: List<RateModel>? = null


    companion object {
        const val ROOT_RATE = 0
        const val OTHER_RATE = 1
    }

    interface OnRateInteraction {
        fun onRateChanged(currencyName: String, value: Float)
        fun onValueChanged(value: Float)
        fun scrollToTop()
    }

    fun setDelegate(callback:OnRateInteraction ){
        this.callback=callback
    }

    init {

        /** We set out TextWatcher here so it can be reused
         *  This will pick up the value being entered in the root rate only
         */
        this.valueWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(newValue: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(newValue: Editable?) {

                val strValue: String = newValue.toString().trim()
                var value: Float

                try {
                    value = strValue.toFloat()
                } catch (e: Exception) {
                    value = 0F
                }

                ratesList!![0].value = value
                callback.onValueChanged(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_rates_layout, parent, false)

        val rateHolder = RateViewHolder(view)

        rateHolder.rateLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(clickedView: View?) {

                val pos: Int = rateHolder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos > 0) {
                    callback.onRateChanged(ratesList!![pos].currency, ratesList!![pos].value)
                }
            }
        });

        return rateHolder
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bindTo(ratesList!![position], position, valueWatcher)
    }

    override fun onBindViewHolder(
        holder: RateViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        val set = payloads.firstOrNull() as Set<String>?

        if (set == null || set.isEmpty()) {
            return super.onBindViewHolder(holder, position, payloads)
        }

        if (set.contains(RatesDiff.VALUE_CHG)) {
            holder.updateValue(ratesList!![position], position)
        }
    }

    override fun getItemCount(): Int {
        return if (ratesList == null) 0 else ratesList!!.size
    }

    /** Return two different types: Root and all others
     *  This makes sure that the user can only change
     *  the rool element's value
     */
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ROOT_RATE else OTHER_RATE
    }

    /** Since the data is coming from Network on a loop
     *  we try to update always with the latest data received
     */
    val pendingList: Deque<List<RateModel>> = LinkedList()

    suspend fun updateList(newRatesList: List<RateModel>) {

        if (ratesList == null) {
            ratesList = newRatesList
            notifyItemRangeInserted(0, newRatesList.size)
            return
        }

        pendingList.push(newRatesList)

        if (pendingList.size > 1)
            return

        calculateDiff(newRatesList)
    }

    /** We call the DiffUtil callback using a coroutine to maximize
     *  UI performance with minimal lag
     */
    private suspend fun calculateDiff(latest: List<RateModel>) {

        val ratesAdapter = this

        // Use Default for CPU intensive tasks
        withContext(Dispatchers.Default) {

            val diffResult = DiffUtil.calculateDiff(RatesDiff(ratesList!!, latest))

            val newRootRate: Boolean = (ratesList!![0].currency != latest[0].currency)
            ratesList = latest

            pendingList.remove(latest)

            // Update UI on the Main Thread
            withContext(Dispatchers.Main) {
                diffResult.dispatchUpdatesTo(ratesAdapter)
                if (newRootRate)
                    callback.scrollToTop()
            }

            if (pendingList.size > 0) {
                // Get the latest data
                calculateDiff(pendingList.pop())
                // Remove possible outdated data so we don't process it
                pendingList.clear()
            }
        }
    }
}