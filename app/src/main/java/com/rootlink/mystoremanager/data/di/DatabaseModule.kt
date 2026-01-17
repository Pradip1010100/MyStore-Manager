package com.rootlink.mystoremanager.data.di

import android.content.Context
import androidx.room.Room
import com.rootlink.mystoremanager.data.database.AppDatabase
import com.rootlink.mystoremanager.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // --------------------------------------------------
    // DATABASE (SINGLETON)
    // --------------------------------------------------
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mystore_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // --------------------------------------------------
    // WORKERS
    // --------------------------------------------------
    @Provides
    fun workerDao(db: AppDatabase): WorkerDao =
        db.workerDao()

    @Provides
    fun workerPaymentDao(db: AppDatabase): WorkerPaymentDao =
        db.workerPaymentDao()

    @Provides
    fun provideWorkerAttendanceDao(db: AppDatabase): WorkerAttendanceDao =
        db.workerAttendanceDao()


    // --------------------------------------------------
    // PRODUCTS & INVENTORY
    // --------------------------------------------------
    @Provides
    fun productCategoryDao(db: AppDatabase): ProductCategoryDao =
        db.productCategoryDao()

    @Provides
    fun productDao(db: AppDatabase): ProductDao =
        db.productDao()

    @Provides
    fun stockDao(db: AppDatabase): StockDao =
        db.stockDao()

    @Provides
    fun stockAdjustmentDao(db: AppDatabase): StockAdjustmentDao =
        db.stockAdjustmentDao()

    // --------------------------------------------------
    // SUPPLIERS & CUSTOMERS
    // --------------------------------------------------
    @Provides
    fun supplierDao(db: AppDatabase): SupplierDao =
        db.supplierDao()

    @Provides
    fun customerDao(db: AppDatabase): CustomerDao =
        db.customerDao()

    // --------------------------------------------------
    // SALES
    // --------------------------------------------------
    @Provides
    fun saleDao(db: AppDatabase): SaleDao =
        db.saleDao()

    @Provides
    fun saleItemDao(db: AppDatabase): SaleItemDao =
        db.saleItemDao()

    @Provides
    fun oldBatteryDao(db: AppDatabase): OldBatteryDao =
        db.oldBatteryDao()

    // --------------------------------------------------
    // PURCHASE
    // --------------------------------------------------
    @Provides
    fun purchaseDao(db: AppDatabase): PurchaseDao =
        db.purchaseDao()

    @Provides
    fun purchaseItemDao(db: AppDatabase): PurchaseItemDao =
        db.purchaseItemDao()

    // --------------------------------------------------
    // ORDERS
    // --------------------------------------------------
    @Provides
    fun orderDao(db: AppDatabase): OrderDao =
        db.orderDao()

    @Provides
    fun orderItemDao(db: AppDatabase): OrderItemDao =
        db.orderItemDao()

    // --------------------------------------------------
    // ACCOUNTING
    // --------------------------------------------------
    @Provides
    fun transactionDao(db: AppDatabase): TransactionDao =
        db.transactionDao()
}
