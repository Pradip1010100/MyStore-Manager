package com.rootlink.mystoremanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*

@Database(
    entities = [
        WorkerEntity::class,
        WorkerPaymentEntity::class,
        WorkerAttendanceEntity::class,

        ProductCategoryEntity::class,
        ProductEntity::class,
        StockEntity::class,
        StockAdjustmentEntity::class,

        SupplierEntity::class,
        CustomerEntity::class,

        SaleEntity::class,
        SaleItemEntity::class,
        OldBatteryEntity::class,

        PurchaseEntity::class,
        PurchaseItemEntity::class,

        OrderEntity::class,
        OrderItemEntity::class,

        TransactionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // ---------- MASTER ----------
    abstract fun workerDao(): WorkerDao
    abstract fun workerPaymentDao(): WorkerPaymentDao
    abstract fun workerAttendanceDao() : WorkerAttendanceDao

    abstract fun productCategoryDao(): ProductCategoryDao
    abstract fun productDao(): ProductDao

    abstract fun supplierDao(): SupplierDao
    abstract fun customerDao(): CustomerDao

    // ---------- INVENTORY ----------
    abstract fun stockDao(): StockDao
    abstract fun stockAdjustmentDao(): StockAdjustmentDao

    // ---------- SALES ----------
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun oldBatteryDao(): OldBatteryDao

    // ---------- PURCHASE ----------
    abstract fun purchaseDao(): PurchaseDao
    abstract fun purchaseItemDao(): PurchaseItemDao

    // ---------- ORDERS ----------
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    // ---------- ACCOUNTING ----------
    abstract fun transactionDao(): TransactionDao
}
