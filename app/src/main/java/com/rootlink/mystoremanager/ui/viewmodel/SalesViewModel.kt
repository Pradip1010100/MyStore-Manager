package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.data.enums.PaymentStatus
import com.rootlink.mystoremanager.data.enums.ProductStatus
import com.rootlink.mystoremanager.data.repository.InventoryRepository
import com.rootlink.mystoremanager.data.repository.SalesRepository
import com.rootlink.mystoremanager.ui.screen.model.ProductForSaleUi
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi
import com.rootlink.mystoremanager.ui.viewmodel.state.SalesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.rootlink.mystoremanager.util.InvoicePdfGenerator

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val salesRepository: SalesRepository,
    private val inventoryRepository: InventoryRepository   // ✅ INJECTED
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState

    private val _products =
        MutableStateFlow<List<ProductForSaleUi>>(emptyList())
    val products: StateFlow<List<ProductForSaleUi>> = _products

    /* ---------------- PRODUCTS (FROM DB) ---------------- */

    fun loadProductsForSale() {
        viewModelScope.launch {
            try {
                val stockOverview =
                    inventoryRepository.getStockOverview()

                _products.value = stockOverview
                    .filter { it.product.status == ProductStatus.ACTIVE }
                    .map { stockUi ->
                        ProductForSaleUi(
                            productId = stockUi.product.productId,
                            name = stockUi.product.name,
                            brand = stockUi.product.brand,
                            unit = stockUi.product.unit,
                            sellingPrice = stockUi.product.sellingPrice,
                            warrantyMonths = stockUi.product.warrantyMonths,
                            availableStock =
                                stockUi.stock.quantityOnHand
                        )
                    }

            } catch (e: Exception) {
                // handle error if you want
            }
        }
    }
    /* ---------------- SALES ---------------- */

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
                // 1️⃣ Sale
                val sale = salesRepository.getSaleById(saleId)

                // 2️⃣ Sale items
                val items = salesRepository.getSaleItems(saleId)

                // 3️⃣ Resolve product names
                val productNameMap = mutableMapOf<Long, String>()

                items
                    .map { it.productId }
                    .distinct()
                    .forEach { productId ->
                        val product = inventoryRepository.getProduct(productId)
                        if (product != null) {
                            productNameMap[productId] = product.name
                        }
                    }

                // 4️⃣ Update UI state
                _uiState.value = _uiState.value.copy(
                    selectedSale = sale,
                    invoiceItems = items,
                    productNameMap = productNameMap,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
            }
        }
    }


    /* ---------------- CREATE SALE ---------------- */

    fun createSale(
        items: List<SaleItemUi>,
        discount: Double,
        oldBatteryAmount: Double?
    ) {
        viewModelScope.launch {
            try {
                val total = items.sumOf { it.quantity * it.price }

                val sale = SaleEntity(
                    saleId = 0L, // ✅ REQUIRED
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
                        productId = it.productId!!,
                        quantity = it.quantity,
                        unitPrice = it.price,
                        lineTotal = it.quantity * it.price
                    )
                }

                val oldBattery = oldBatteryAmount?.let {
                    OldBatteryEntity(
                        oldBatteryId = 0L,
                        saleId = 0L,
                        batteryType = "",
                        quantity = 0,
                        weight = 0.0,
                        rate = 0.0,
                        amount = it
                    )
                }

                salesRepository.createSale(
                    sale,
                    saleItems,
                    oldBattery
                )

                loadSales()

            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message)
            }
        }
    }


    //Share PDF

    fun shareInvoicePdf(
        context: Context,
        sale: SaleEntity,
        items: List<SaleItemEntity>
    ) {
        val pdfFile = InvoicePdfGenerator.generate(
            context,
            sale,
            items,
            _uiState.value.productNameMap
        )


        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share Invoice")
        )
    }
}
