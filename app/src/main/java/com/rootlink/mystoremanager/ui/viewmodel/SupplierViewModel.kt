package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.entity.SupplierPaymentEntity
import com.rootlink.mystoremanager.data.enums.SupplierLedgerType
import com.rootlink.mystoremanager.data.repository.SupplierRepository
import com.rootlink.mystoremanager.ui.screen.model.PurchaseItemUi
import com.rootlink.mystoremanager.ui.screen.model.SupplierLedgerUiItem
import com.rootlink.mystoremanager.ui.screen.model.SupplierPurchaseUi
import com.rootlink.mystoremanager.ui.screen.model.SupplierTotals
import com.rootlink.mystoremanager.util.toReadableDate
import com.rootlink.mystoremanager.util.toReadableDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val repo: SupplierRepository
) : ViewModel() {

    val suppliers = MutableStateFlow<List<Pair<SupplierEntity, Double>>>(emptyList())
    val purchaseHistory = MutableStateFlow<List<SupplierPurchaseUi>>(emptyList())
    val products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val ledgerUi = MutableStateFlow<List<SupplierLedgerUiItem>>(emptyList())
    val totalPurchased = MutableStateFlow(0.0)
    val totalPaid = MutableStateFlow(0.0)
    val due = MutableStateFlow(0.0)

    val purchaseItems = MutableStateFlow<List<PurchaseItemUi>>(emptyList())

    val totalAmount = purchaseItems
        .map { it.sumOf { i -> i.lineTotal } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    private val _suppliersWithDue =
        MutableStateFlow<List<Pair<SupplierEntity, Double>>>(emptyList())
    val suppliersWithDue = _suppliersWithDue.asStateFlow()

    val supplier = MutableStateFlow<SupplierEntity?>(null)
    val totals = MutableStateFlow<SupplierTotals?>(null)

    fun loadSupplierProfile(supplierId: Long) = viewModelScope.launch {
        supplier.value = repo.getSupplierById(supplierId)
        totals.value = repo.getSupplierTotals(supplierId)
    }

    fun updateSupplier(updated: SupplierEntity) = viewModelScope.launch {
        repo.updateSupplier(updated)
        supplier.value = updated
    }

    fun deactivateSupplier(id: Long) = viewModelScope.launch {
        repo.deactivateSupplier(id)
        loadSupplierProfile(id)
    }

    fun activateSupplier(id: Long) = viewModelScope.launch {
        repo.activateSupplier(id)
        loadSupplierProfile(id)
    }

    fun loadSupplierDetail(supplierId: Long) = viewModelScope.launch {
        val totals = repo.getSupplierTotals(supplierId)

        totalPurchased.value = totals.totalPurchased
        totalPaid.value = totals.totalPaid
        due.value = totals.due

        purchaseHistory.value = repo.getSupplierPurchaseHistory(supplierId)
    }

    fun loadSuppliersWithDue() {
        viewModelScope.launch {
            _suppliersWithDue.value =
                repo.getSuppliersWithDue()
        }
    }

    fun addSupplier(supplier: SupplierEntity) = viewModelScope.launch {
        repo.addSupplier(supplier = supplier)
    }

    fun loadSuppliers() = viewModelScope.launch {
        suppliers.value = repo.getSuppliersWithDue()
    }

    fun loadProducts() = viewModelScope.launch {
        products.value = repo.getActiveProducts()
    }

    fun loadLedger(supplierId: Long) = viewModelScope.launch {

        val raw = repo.getSupplierLedger(supplierId)

        var runningDue = 0.0

        ledgerUi.value = raw.map {
            when (it.type) {
                SupplierLedgerType.PURCHASE -> {
                    runningDue += it.debit
                    SupplierLedgerUiItem(
                        date = it.date.toReadableDateTime(),
                        reference = "PUR#${it.referenceId}",
                        label = "Purchase",
                        amount = it.debit,
                        dueAfter = runningDue
                    )
                }

                SupplierLedgerType.PAYMENT -> {
                    runningDue -= it.credit
                    SupplierLedgerUiItem(
                        date = it.date.toReadableDateTime(),
                        reference = "PAY#${it.referenceId}",
                        label = "Payment",
                        amount = -it.credit,
                        dueAfter = runningDue
                    )
                }
            }
        }

        due.value = runningDue
    }


    fun addPurchaseItem(item: PurchaseItemUi) {
        purchaseItems.value += item
    }
    fun savePurchase(
        supplierId: Long,
        paidAmount: Double
    ) = viewModelScope.launch {

        repo.recordPurchase(
            supplierId = supplierId,
            items = purchaseItems.value,
            paidAmount = paidAmount
        )

        clearPurchaseDraft()           // ✅ now works
        loadSupplierDetail(supplierId) // ✅ reload history
        loadLedger(supplierId)         // ✅ reload ledger & due
    }

    fun removePurchaseItem(i: Int) {
        purchaseItems.value = purchaseItems.value.toMutableList().also { it.removeAt(i) }
    }

    fun paySupplier(payment: SupplierPaymentEntity) =
        viewModelScope.launch { repo.paySupplier(payment) }
    /* ---------------- PURCHASE DRAFT ---------------- */

    fun clearPurchaseDraft() {
        purchaseItems.value = emptyList()
    }
}
