package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.StockEntity
import com.rootlink.mystoremanager.ui.screen.model.StockUi

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stock: StockEntity)

    @Query("SELECT * FROM stock WHERE productId = :productId")
    suspend fun getStock(productId: Long): StockEntity?

    @Query("""
        UPDATE stock
        SET quantityOnHand = :newQty,
            lastUpdated = :time
        WHERE productId = :productId
    """)
    suspend fun setStock(
        productId: Long,
        newQty: Double,
        time: Long
    )

    @Query("""
        UPDATE stock 
        SET quantityOnHand = quantityOnHand + :delta,
            lastUpdated = :time
        WHERE productId = :productId
    """)
    suspend fun updateStock(
        productId: Long,
        delta: Int,
        time: Long
    )
    @Query("SELECT * FROM stock")
    suspend fun getAll(): List<StockEntity>


    @Query("""
    SELECT 
        p.*,
        s.productId AS stock_productId,
        s.quantityOnHand AS stock_quantityOnHand,
        s.lastUpdated AS stock_lastUpdated
    FROM products p
    INNER JOIN stock s ON p.productId = s.productId
    WHERE p.status = 'ACTIVE'
    ORDER BY p.name
""")
    suspend fun getStockOverview(): List<StockUi>


    @Query("""
    SELECT 
        p.*,
        s.productId AS stock_productId,
        s.quantityOnHand AS stock_quantityOnHand,
        s.lastUpdated AS stock_lastUpdated
    FROM products p
    INNER JOIN stock s ON p.productId = s.productId
    WHERE p.status = 'ACTIVE'
      AND s.quantityOnHand <= :limit
""")
    suspend fun getLowStock(limit: Double): List<StockUi>


}
