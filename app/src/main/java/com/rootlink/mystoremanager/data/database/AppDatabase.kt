package com.rootlink.mystoremanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*

const val DB_NAME = "myStoreManager"

@Database(
    entities = [
        CompanyProfileEntity::class,
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
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyProfileDao(): CompanyProfileDao
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
