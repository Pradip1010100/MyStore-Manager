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
import com.rootlink.mystoremanager.data.entity.CustomerEntity
import com.rootlink.mystoremanager.data.repository.CompanyRepository
import com.rootlink.mystoremanager.util.InvoicePdfGenerator

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val salesRepository: SalesRepository,
    private val companyRepository: CompanyRepository,
    private val inventoryRepository: InventoryRepository   // ✅ INJECTED
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState

    private val _products =
        MutableStateFlow<List<ProductForSaleUi>>(emptyList())
    val products: StateFlow<List<ProductForSaleUi>> = _products

    private val _customers = MutableStateFlow<List<CustomerEntity>>(emptyList())
    val customers : StateFlow<List<CustomerEntity>> = _customers

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

    fun loadCustomers(){
        viewModelScope.launch {
            _customers.value = salesRepository.getAllCustomers()
        }
    }
    /* ---------------- SALES ---------------- */

    fun loadSales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val sales = salesRepository.getAllSales()

                // 1️⃣ Collect unique customerIds
                val customerIds = sales
                    .mapNotNull { it.customerId }
                    .distinct()

                // 2️⃣ Load customers into map
                val customerMap = mutableMapOf<Long, CustomerEntity>()
                for (id in customerIds) {
                    val customer = salesRepository.getCustomerById(id)
                    customerMap[id] = customer
                }

                // 3️⃣ Update state
                _uiState.value = _uiState.value.copy(
                    sales = sales,
                    customerMap = customerMap,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }


    fun loadInvoice(saleId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val sale = salesRepository.getSaleById(saleId)
                val items = salesRepository.getSaleItems(saleId)

                val productNameMap = mutableMapOf<Long, String>()
                items.map { it.productId }.distinct().forEach { id ->
                    inventoryRepository.getProduct(id)?.let {
                        productNameMap[id] = it.name
                    }
                }

                val customer =
                    sale.customerId?.let {
                        salesRepository.getCustomerById(it)
                    }
                val oldBattery =
                    salesRepository.getOldBatteryBySaleId(saleId)

                val company = companyRepository.getCompany()

                _uiState.value = _uiState.value.copy(
                    selectedSale = sale,
                    selectedCustomer = customer,
                    invoiceItems = items,
                    productNameMap = productNameMap,
                    oldBatteryAmount = oldBattery?.amount,
                    companyProfile = company, // ✅
                    isLoading = false
                )


            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }



    /* ---------------- CREATE SALE ---------------- */

    fun createSale(
        items: List<SaleItemUi>,
        discount: Double,
        hasOldBattery: Boolean,
        batteryType: String,
        batteryQty: Int,
        batteryWeight: Double,
        batteryRate: Double,
        batteryAmount: Double,
        existingCustomer: CustomerEntity?,
        manualName: String,
        manualPhone: String,
        manualAddress: String
    ) {
        viewModelScope.launch {
            try {

                val customerId =
                    when {
                        existingCustomer != null ->
                            existingCustomer.customerId

                        manualName.isNotBlank() ->
                            salesRepository.insertCustomer(
                                CustomerEntity(
                                    name = manualName,
                                    phone = manualPhone,
                                    address = manualAddress.ifBlank { null }
                                )
                            )

                        else -> null
                    }

                val total = items.sumOf { it.quantity * it.price }

                val sale = SaleEntity(
                    saleId = 0L,
                    saleDate = System.currentTimeMillis(),
                    customerId = customerId,
                    totalAmount = total,
                    discount = discount,
                    finalAmount =
                        total - discount - if (hasOldBattery) batteryAmount else 0.0,
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

                val oldBattery =
                    if (hasOldBattery && batteryAmount > 0) {
                        OldBatteryEntity(
                            oldBatteryId = 0L,
                            saleId = 0L,
                            batteryType = batteryType,
                            quantity = batteryQty,
                            weight = batteryWeight,
                            rate = batteryRate,
                            amount = batteryAmount
                        )
                    } else null

                salesRepository.createSale(
                    sale = sale,
                    items = saleItems,
                    oldBattery = oldBattery
                )

                loadSales()

            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = e.message)
            }
        }
    }


    fun loadCompanyProfile() {
        viewModelScope.launch {
            val company = companyRepository.getCompany()
            _uiState.value = _uiState.value.copy(companyProfile = company)
        }
    }

    //Share PDF

    fun shareInvoicePdf(
        context: Context,
        sale: SaleEntity,
        items: List<SaleItemEntity>
    ) {
        val state = _uiState.value
        val company = state.companyProfile
            ?: throw IllegalStateException("Company profile not found")

        val pdfFile = InvoicePdfGenerator.generate(
            context = context,
            sale = sale,
            company = company, // ✅ FIX
            customer = state.selectedCustomer,
            items = items,
            productNameMap = state.productNameMap,
            oldBatteryAmount = state.oldBatteryAmount
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Invoice"))
    }

}
