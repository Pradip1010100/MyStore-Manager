package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.repository.ReportRepository
import com.rootlink.mystoremanager.data.repository.TransactionRepository
import com.rootlink.mystoremanager.ui.viewmodel.state.TransactionUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AccountingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transactions =
        MutableStateFlow<List<TransactionUiItem>>(emptyList())

    val transactions: StateFlow<List<TransactionUiItem>> =
        _transactions.asStateFlow()

    fun loadTodayTransactions() {
        viewModelScope.launch {
            val todayStart =
                java.time.LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            val now = System.currentTimeMillis()

            _transactions.value =
                transactionRepository.getTransactionUiItems(
                    todayStart,
                    now
                )
        }
    }
}