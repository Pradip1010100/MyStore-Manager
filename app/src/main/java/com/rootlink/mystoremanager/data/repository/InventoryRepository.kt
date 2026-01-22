package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.ProductCategoryDao
import com.rootlink.mystoremanager.data.dao.ProductDao
import com.rootlink.mystoremanager.data.dao.StockAdjustmentDao
import com.rootlink.mystoremanager.data.dao.StockDao
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.entity.ProductCategoryEntity
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity
import com.rootlink.mystoremanager.data.entity.StockEntity
import com.rootlink.mystoremanager.data.enums.ProductStatus
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val productDao: ProductDao,
    private val productCategoryDao: ProductCategoryDao,
    private val stockDao: StockDao,
    private val stockAdjustmentDao: StockAdjustmentDao,
    private val transactionDao: TransactionDao
) {

    /* ---------- CATEGORY ---------- */

    suspend fun addCategory(category: ProductCategoryEntity) =
        productCategoryDao.insert(category)

    suspend fun getActiveCategories() =
        productCategoryDao.getActive()

    /* ---------- PRODUCT ---------- */

    @Transaction
    suspend fun addProduct(product: ProductEntity) {
        val productId = productDao.insert(product)

        // 🚨 IMPORTANT: initialize stock
        stockDao.insert(
            StockEntity(
                productId = productId,
                quantityOnHand = 0.0,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
    suspend fun deactivateProduct(productId: Long) =
        productDao.deactivate(productId)
    suspend fun activateProduct(productId: Long) {
        productDao.activate(productId)
    }

    suspend fun getAllProducts() =
        productDao.getAllProducts()

    suspend fun getProducts() =
        productDao.getActive()

    suspend fun getProduct(productId: Long) =
        productDao.getById(productId)
    /* ---------- STOCK ---------- */
    suspend fun getStockOverview() =
        stockDao.getStockOverview()

    suspend fun getLowStock(limit: Double) =
        stockDao.getLowStock(limit)

    @Transaction
    suspend fun adjustStock(adjustment: StockAdjustmentEntity) {

        val stock = stockDao.getStock(adjustment.productId)
            ?: throw IllegalStateException("Stock row missing")

        val currentQty = stock.quantityOnHand

        val newQty = when (adjustment.adjustmentType) {
            StockAdjustmentType.IN ->
                currentQty + adjustment.quantity

            StockAdjustmentType.OUT -> {
                if (currentQty < adjustment.quantity) {
                    throw IllegalArgumentException("Insufficient stock")
                }
                currentQty - adjustment.quantity
            }
        }

        stockDao.setStock(
            productId = adjustment.productId,
            newQty = newQty,
            time = System.currentTimeMillis()
        )

        stockAdjustmentDao.insert(adjustment)
    }


    suspend fun getStockHistory(productId: Long) =
        stockAdjustmentDao.getHistoryForProduct(productId)

    suspend fun getAllStock() =
        stockDao.getAll()
}
