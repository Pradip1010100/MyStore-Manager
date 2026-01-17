package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    fun createOrder(/* order + items */) {
        viewModelScope.launch {
            // orderRepository.createOrder(...)
        }
    }

    fun convertOrderToSale(/* params */) {
        viewModelScope.launch {
            // orderRepository.convertOrderToSale(...)
        }
    }
}