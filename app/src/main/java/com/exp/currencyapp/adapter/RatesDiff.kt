package com.exp.currencyapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.exp.currencyapp.model.RateModel


/**
 * Calculate the difference between list of rates
 * The only payload we need is for when the value is updated
 * With Kotlin comparing items is easier since we declared
 * RateModel as a data class
 */
class RatesDiff(private val oldList: List<RateModel>, private val newList: List<RateModel>) :
    DiffUtil.Callback() {

    companion object {
        const val VALUE_CHG = "VALUE_CHG"
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currency == newList[newItemPosition].currency
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldRate = oldList[oldItemPosition]
        val newRate = newList[newItemPosition]

        val payloadSet = mutableSetOf<String>()

        if (oldRate.value != newRate.value)
            payloadSet.add(VALUE_CHG)

        return payloadSet
    }
}