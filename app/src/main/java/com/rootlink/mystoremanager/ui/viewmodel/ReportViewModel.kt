package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.ReportRepository
import kotlinx.coroutines.launch

class ReportViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    fun loadSalesReport() {
        viewModelScope.launch {
            // reportRepository.getSalesReport(...)
        }
    }

    fun loadProfitLoss() {
        viewModelScope.launch {
            // reportRepository.getProfitLoss(...)
        }
    }
}