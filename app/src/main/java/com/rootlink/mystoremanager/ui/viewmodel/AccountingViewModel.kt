package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.dao.TransactionDao
import kotlinx.coroutines.launch

class AccountingViewModel(
    private val transactionDao: TransactionDao
) : ViewModel() {

    fun loadTransactions() {
        viewModelScope.launch {
            //transactionDao.getByDateRange(/* from, to */)
        }
    }
}