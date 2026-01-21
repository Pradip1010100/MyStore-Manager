package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.PersonalTransactionEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.TransactionType
import com.rootlink.mystoremanager.data.repository.PersonalTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PersonalTransactionViewModel @Inject constructor(
    private val repository: PersonalTransactionRepository
) : ViewModel() {

    private val _history =
        MutableStateFlow<List<PersonalTransactionEntity>>(emptyList())
    val history: StateFlow<List<PersonalTransactionEntity>> =
        _history.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _history.value = repository.getAll()
        }
    }

    fun save(
        amount: Double,
        title: String,
        person: String?,
        direction: TransactionType,
        paymentMode: PaymentMode,
        note: String?
    ) {
        viewModelScope.launch {
            repository.addPersonalTransaction(
                PersonalTransactionEntity(
                    amount = amount,
                    title = title,
                    personName = person,
                    direction = direction,
                    paymentMode = paymentMode,
                    note = note,
                    date = System.currentTimeMillis()
                )
            )
            loadHistory() // 🔥 refresh instantly
        }
    }
}
