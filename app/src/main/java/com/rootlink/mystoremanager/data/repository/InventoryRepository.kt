package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.StockAdjustmentDao
import com.rootlink.mystoremanager.data.dao.StockDao
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType
import com.rootlink.mystoremanager.data.enums.TransactionCategory
import com.rootlink.mystoremanager.data.enums.TransactionReferenceType
import com.rootlink.mystoremanager.data.enums.TransactionType
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val stockDao: StockDao,
    private val stockAdjustmentDao: StockAdjustmentDao,
    private val transactionDao: TransactionDao
) {

    @Transaction
    suspend fun adjustStock(
        adjustment: StockAdjustmentEntity,
        hasFinancialImpact: Boolean,
        amount: Double?
    ) {
        val delta =
            if (adjustment.adjustmentType == StockAdjustmentType.IN)
                adjustment.quantity
            else
                -adjustment.quantity

        stockDao.updateStock(
            productId = adjustment.productId,
            delta = delta,
            time = System.currentTimeMillis()
        )

        stockAdjustmentDao.insert(adjustment)

        if (hasFinancialImpact && amount != null) {
            transactionDao.insert(
                TransactionEntity(
                    transactionDate = System.currentTimeMillis(),
                    transactionType =
                        if (delta > 0)
                            TransactionType.IN
                        else
                            TransactionType.OUT,
                    category = TransactionCategory.ADJUSTMENT,
                    amount = amount,
                    paymentMode = PaymentMode.CASH,
                    referenceId = adjustment.adjustmentId,
                    notes = adjustment.reason,
                    transactionId = 0,
                    referenceType = TransactionReferenceType.SALE
                )
            )
        }
    }
}
