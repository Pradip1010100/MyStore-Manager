package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*
import com.rootlink.mystoremanager.data.enums.OrderStatus
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.TransactionCategory
import com.rootlink.mystoremanager.data.enums.TransactionReferenceType
import com.rootlink.mystoremanager.data.enums.TransactionType
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val stockDao: StockDao,
    private val transactionDao: TransactionDao
) {

    // ---------------------------
    // CREATE ORDER
    // ---------------------------
    @Transaction
    suspend fun createOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        paymentMode: PaymentMode
    ) {
        require(items.isNotEmpty()) {
            "Order must have at least one item"
        }

        val orderId = orderDao.insert(order)

        orderItemDao.insertAll(
            items.map { it.copy(orderId = orderId) }
        )

        // Advance payment (optional)
        if (order.advanceAmount > 0) {
            transactionDao.insert(
                TransactionEntity(
                    transactionDate = System.currentTimeMillis(),
                    transactionType = TransactionType.IN,
                    category = TransactionCategory.ADVANCE,
                    amount = order.advanceAmount,
                    paymentMode = paymentMode,
                    referenceId = orderId,
                    notes = "Order advance",
                    referenceType = TransactionReferenceType.SALE
                )
            )
        }
    }

    // ---------------------------
    // CONVERT ORDER TO SALE
    // ---------------------------
    @Transaction
    suspend fun convertOrderToSale(
        order: OrderEntity,
        orderItems: List<OrderItemEntity>,
        sale: SaleEntity,
        paymentMode: PaymentMode
    ) {
        // 1. Create sale
        val saleId = saleDao.insert(sale)

        // 2. Create sale items
        val saleItems = orderItems.map {
            SaleItemEntity(
                saleId = saleId,
                productId = it.productId,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                lineTotal = it.quantity * it.unitPrice
            )
        }

        saleItemDao.insertAll(saleItems)

        // 3. Deduct stock NOW (not earlier)
        saleItems.forEach {
            stockDao.updateStock(
                productId = it.productId,
                delta = -it.quantity,
                time = System.currentTimeMillis()
            )
        }

        // 4. Adjust advance against final bill
        val balanceAmount = sale.finalAmount - order.advanceAmount

        if (balanceAmount > 0) {
            transactionDao.insert(
                TransactionEntity(
                    transactionDate = System.currentTimeMillis(),
                    transactionType = TransactionType.IN,
                    category = TransactionCategory.SALE,
                    amount = balanceAmount,
                    paymentMode = paymentMode,
                    referenceId = saleId,
                    notes = "Order balance payment",
                    referenceType = TransactionReferenceType.SALE
                )
            )
        }

        // 5. Update order status
        orderDao.update(
            order.copy(status = OrderStatus.COMPLETED)
        )
    }
}
