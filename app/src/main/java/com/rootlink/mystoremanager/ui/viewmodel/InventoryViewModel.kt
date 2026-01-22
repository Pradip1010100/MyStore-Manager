package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.ProductCategoryEntity
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity
import com.rootlink.mystoremanager.data.enums.ProductStatus
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType
import com.rootlink.mystoremanager.data.repository.InventoryRepository
import com.rootlink.mystoremanager.ui.screen.model.StockUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    /* ---------- CATEGORIES ---------- */

    private val _categories = MutableStateFlow<List<ProductCategoryEntity>>(emptyList())
    val categories = _categories.asStateFlow()

    fun loadCategories() = viewModelScope.launch {
        _categories.value = repository.getActiveCategories()
    }

    fun addCategory(name: String) = viewModelScope.launch {
        repository.addCategory(
            ProductCategoryEntity(
                name = name,
                description = null,
                status = ProductStatus.ACTIVE
            )
        )
        loadCategories()
    }

    /* ---------- PRODUCTS ---------- */

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products = _products.asStateFlow()

    fun loadProducts() = viewModelScope.launch {
        _products.value = repository.getAllProducts()
    }
    fun deactivateProduct(productId: Long) = viewModelScope.launch {
        repository.deactivateProduct(productId)
        loadProducts()
        loadStockOverview()
    }
    fun activateProduct(productId: Long) = viewModelScope.launch {
        repository.activateProduct(productId)
        loadProducts()
        loadStockOverview()
    }

    fun addProduct(
        name: String,
        categoryId: Long,
        brand: String,
        unit: String,
        purchasePrice: Double,
        sellingPrice: Double,
        warrantyMonths: Int
    ) = viewModelScope.launch {
        repository.addProduct(
            ProductEntity(
                name = name,
                categoryId = categoryId,
                brand = brand,
                unit = unit,
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                warrantyMonths = warrantyMonths,
                status = ProductStatus.ACTIVE
            )
        )
        loadProducts()
    }

    /* ---------- STOCK ---------- */
    private val _stockOverview =
        MutableStateFlow<List<StockUi>>(emptyList())
    val stockOverview = _stockOverview.asStateFlow()
    private val _lowStock =
        MutableStateFlow<List<StockUi>>(emptyList())
    val lowStock = _lowStock.asStateFlow()

    fun loadLowStock(limit: Double = 5.0) = viewModelScope.launch {
        _lowStock.value = repository.getLowStock(limit)
    }

    fun loadStockOverview() = viewModelScope.launch {
        _stockOverview.value = repository.getStockOverview()
    }
    /* ---------- STOCK HISTORY ---------- */

    private val _stockHistory =
        MutableStateFlow<List<StockAdjustmentEntity>>(emptyList())
    val stockHistory = _stockHistory.asStateFlow()

    fun loadStockHistory(productId: Long) =
        viewModelScope.launch {
            _stockHistory.value =
                repository.getStockHistory(productId)
        }

    fun adjustStock(
        productId: Long,
        type: StockAdjustmentType,
        quantity: Int,
        reason: String
    ) = viewModelScope.launch {

        repository.adjustStock(
            StockAdjustmentEntity(
                productId = productId,
                adjustmentType = type,
                quantity = quantity.toDouble(),
                reason = reason,
                adjustmentDate = System.currentTimeMillis()
            )
        )
    }
}
