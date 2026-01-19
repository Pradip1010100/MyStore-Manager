package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity

data class SalesUiState(
    val isLoading: Boolean = false,
    val sales: List<SaleEntity> = emptyList(),
    val invoiceItems: List<SaleItemEntity> = emptyList(),
    val selectedSale: SaleEntity? = null,
    val productNameMap: Map<Long, String> = emptyMap(),

    val error: String? = null
)