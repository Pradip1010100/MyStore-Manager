package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.SalesRepository
import kotlinx.coroutines.launch

class SalesViewModel(
    private val salesRepository: SalesRepository
) : ViewModel() {

    fun createSale(/* sale + items */) {
        viewModelScope.launch {
            // salesRepository.createSale(...)
        }
    }
}