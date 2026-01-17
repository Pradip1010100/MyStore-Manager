package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.data.enums.PaymentStatus
import com.rootlink.mystoremanager.data.repository.SalesRepository
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi
import com.rootlink.mystoremanager.ui.viewmodel.state.SalesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SalesViewModel @Inject constructor(
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    fun loadSales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                _uiState.value = _uiState.value.copy(
                    sales = salesRepository.getAllSales(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun loadInvoice(saleId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                _uiState.value = _uiState.value.copy(
                    selectedSale = salesRepository.getSaleById(saleId),
                    invoiceItems = salesRepository.getSaleItems(saleId),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createSale(
        items: List<SaleItemUi>,
        discount: Double,
        oldBatteryAmount: Double?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val total = items.sumOf { it.quantity * it.price }

                val sale = SaleEntity(
                    saleId = 0L,
                    saleDate = System.currentTimeMillis(),
                    customerId = null,
                    totalAmount = total,
                    discount = discount,
                    finalAmount = total - discount,
                    paymentStatus = PaymentStatus.PAID
                )

                val saleItems = items.map {
                    SaleItemEntity(
                        saleItemId = 0L,
                        saleId = 0L,
                        productId = it.productId,
                        quantity = it.quantity,
                        unitPrice = it.price,
                        lineTotal = it.quantity * it.price
                    )
                }

                val oldBattery = oldBatteryAmount?.let {
                    OldBatteryEntity(
                        oldBatteryId = 0L,
                        saleId = 0L,
                        amount = it,
                        batteryType = "",
                        quantity = 0,
                        weight = 0.0,
                        rate = 0.0
                    )
                }

                salesRepository.createSale(sale, saleItems, oldBattery)
                loadSales()

            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
