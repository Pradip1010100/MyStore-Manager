package com.rootlink.mystoremanager.ui.screen.model

data class KpiUi(
    val title: String,
    val value: String,
    val deltaPercent: Double?, // +12.5 / -8.0
    val isPositive: Boolean,
    val onClick: () -> Unit
)
