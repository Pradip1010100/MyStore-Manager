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
    // DATABASE
    // --------------------------------------------------
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "myStoreManager.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideCompanyProfileDao(db: AppDatabase) : CompanyProfileDao = db.companyProfileDao()

    // --------------------------------------------------
    // WORKERS
    // --------------------------------------------------
    @Provides
    fun provideWorkerDao(db: AppDatabase): WorkerDao =
        db.workerDao()

    @Provides
    fun provideWorkerPaymentDao(db: AppDatabase): WorkerPaymentDao =
        db.workerPaymentDao()

    @Provides
    fun provideWorkerAttendanceDao(db: AppDatabase): WorkerAttendanceDao =
        db.workerAttendanceDao()

    // --------------------------------------------------
    // PRODUCTS & INVENTORY
    // --------------------------------------------------
    @Provides
    fun provideProductCategoryDao(db: AppDatabase): ProductCategoryDao =
        db.productCategoryDao()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao =
        db.productDao()

    @Provides
    fun provideStockDao(db: AppDatabase): StockDao =
        db.stockDao()

    @Provides
    fun provideStockAdjustmentDao(db: AppDatabase): StockAdjustmentDao =
        db.stockAdjustmentDao()

    // --------------------------------------------------
    // SUPPLIERS
    // --------------------------------------------------
    @Provides
    fun provideSupplierDao(db: AppDatabase): SupplierDao =
        db.supplierDao()

    @Provides
    fun provideSupplierPaymentDao(db: AppDatabase): SupplierPaymentDao =
        db.supplierPaymentDao()

    @Provides
    fun providePurchaseDao(db: AppDatabase): PurchaseDao =
        db.purchaseDao()

    @Provides
    fun providePurchaseItemDao(db: AppDatabase): PurchaseItemDao =
        db.purchaseItemDao()

    // --------------------------------------------------
    // CUSTOMERS
    // --------------------------------------------------
    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao =
        db.customerDao()

    // --------------------------------------------------
    // SALES
    // --------------------------------------------------
    @Provides
    fun provideSaleDao(db: AppDatabase): SaleDao =
        db.saleDao()

    @Provides
    fun provideSaleItemDao(db: AppDatabase): SaleItemDao =
        db.saleItemDao()

    @Provides
    fun provideOldBatteryDao(db: AppDatabase): OldBatteryDao =
        db.oldBatteryDao()

    // --------------------------------------------------
    // ORDERS
    // --------------------------------------------------
    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao =
        db.orderDao()

    @Provides
    fun provideOrderItemDao(db: AppDatabase): OrderItemDao =
        db.orderItemDao()

    // --------------------------------------------------
    // ACCOUNTING
    // --------------------------------------------------
    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao =
        db.transactionDao()

    @Provides
    fun providePersonalTransactionDao(db: AppDatabase): PersonalTransactionDao =
        db.personalTransactionDao()
}
