package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    fun adjustStock(/* adjustment */) {
        viewModelScope.launch {
            // inventoryRepository.adjustStock(...)
        }
    }
}