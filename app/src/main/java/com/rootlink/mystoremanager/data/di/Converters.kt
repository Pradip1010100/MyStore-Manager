package com.rootlink.mystoremanager.data.database

import androidx.room.TypeConverter
import com.rootlink.mystoremanager.data.enums.*

class Converters {

    /* ---------- TransactionType ---------- */
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        TransactionType.valueOf(value)

    /* ---------- PaymentMode ---------- */
    @TypeConverter
    fun fromPaymentMode(value: PaymentMode): String = value.name

    @TypeConverter
    fun toPaymentMode(value: String): PaymentMode =
        PaymentMode.valueOf(value)

    /* ---------- TransactionCategory ---------- */
    @TypeConverter
    fun fromTransactionCategory(value: TransactionCategory): String = value.name

    @TypeConverter
    fun toTransactionCategory(value: String): TransactionCategory =
        TransactionCategory.valueOf(value)

    /* ---------- TransactionReferenceType ---------- */
    @TypeConverter
    fun fromReferenceType(value: TransactionReferenceType): String = value.name

    @TypeConverter
    fun toReferenceType(value: String): TransactionReferenceType =
        TransactionReferenceType.valueOf(value)
}
