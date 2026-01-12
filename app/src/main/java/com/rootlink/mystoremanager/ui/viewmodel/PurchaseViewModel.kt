package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.PurchaseRepository
import kotlinx.coroutines.launch

class PurchaseViewModel(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    fun recordPurchase(/* purchase + items */) {
        viewModelScope.launch {
            // purchaseRepository.recordPurchase(...)
        }
    }
}
