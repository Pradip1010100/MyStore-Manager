package com.rootlink.mystoremanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*

@Database(
    entities = [

        // ---------- WORKERS ----------
        WorkerEntity::class,
        WorkerPaymentEntity::class,
        WorkerAttendanceEntity::class,

        // ---------- PRODUCTS & INVENTORY ----------
        ProductCategoryEntity::class,
        ProductEntity::class,
        StockEntity::class,
        StockAdjustmentEntity::class,

        // ---------- SUPPLIERS & CUSTOMERS ----------
        SupplierEntity::class,
        SupplierPaymentEntity::class,   // ✅ ADDED
        CustomerEntity::class,

        // ---------- SALES ----------
        SaleEntity::class,
        SaleItemEntity::class,
        OldBatteryEntity::class,

        // ---------- PURCHASE ----------
        PurchaseEntity::class,
        PurchaseItemEntity::class,

        // ---------- ORDERS ----------
        OrderEntity::class,
        OrderItemEntity::class,

        // ---------- ACCOUNTING ----------
        TransactionEntity::class,
        PersonalTransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // ---------- WORKERS ----------
    abstract fun workerDao(): WorkerDao
    abstract fun workerPaymentDao(): WorkerPaymentDao
    abstract fun workerAttendanceDao(): WorkerAttendanceDao

    // ---------- PRODUCTS ----------
    abstract fun productCategoryDao(): ProductCategoryDao
    abstract fun productDao(): ProductDao

    // ---------- INVENTORY ----------
    abstract fun stockDao(): StockDao
    abstract fun stockAdjustmentDao(): StockAdjustmentDao

    // ---------- SUPPLIERS ----------
    abstract fun supplierDao(): SupplierDao
    abstract fun supplierPaymentDao(): SupplierPaymentDao   // ✅ ADDED
    abstract fun purchaseDao(): PurchaseDao
    abstract fun purchaseItemDao(): PurchaseItemDao

    // ---------- CUSTOMERS ----------
    abstract fun customerDao(): CustomerDao

    // ---------- SALES ----------
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun oldBatteryDao(): OldBatteryDao

    // ---------- ORDERS ----------
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    // ---------- ACCOUNTING ----------
    abstract fun transactionDao(): TransactionDao
    abstract fun personalTransactionDao(): PersonalTransactionDao
}
