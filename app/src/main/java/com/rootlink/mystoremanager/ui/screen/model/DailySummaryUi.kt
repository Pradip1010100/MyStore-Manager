package com.rootlink.mystoremanager.ui.screen.model

data class DailySummaryUi(
    val totalIn: Double,
    val totalOut: Double
) {
    val net: Double get() = totalIn - totalOut
}