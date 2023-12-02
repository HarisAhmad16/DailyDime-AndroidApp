package com.cmpt362team21.ui.investments

import androidx.lifecycle.ViewModel

class InvestmentsViewModel : ViewModel() {
    val stocks = mutableListOf<Stock>()

    fun addStock(stock: Stock) {
        stocks.add(stock)
    }

    fun deleteStock(stock: Stock) {
        stocks.remove(stock)
    }
}
