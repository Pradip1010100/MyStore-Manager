package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
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