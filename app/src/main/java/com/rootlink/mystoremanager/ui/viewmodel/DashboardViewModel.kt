package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.repository.DashboardRepository
import com.rootlink.mystoremanager.ui.screen.model.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _suppliers =
        MutableStateFlow<List<SupplierEntity>>(emptyList())
    val suppliers: StateFlow<List<SupplierEntity>> =
        _suppliers.asStateFlow()

    private val _workers =
        MutableStateFlow<List<WorkerEntity>>(emptyList())
    val workers: StateFlow<List<WorkerEntity>> =
        _workers.asStateFlow()

    fun loadSuppliers() {
        viewModelScope.launch {
            _suppliers.value = repository.getSuppliers()
        }
    }

    fun loadWorkers() {
        viewModelScope.launch {
            _workers.value = repository.getWorkers()
        }
    }
    fun loadTodayDashboard() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val zone = ZoneId.systemDefault()

                val start =
                    java.time.LocalDate.now()
                        .atStartOfDay(zone)
                        .toInstant()
                        .toEpochMilli()

                val end = System.currentTimeMillis()

                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        todaySalesAmount =
                            repository.getTodaySalesAmount(start, end),
                        todaySalesCount =
                            repository.getTodaySalesCount(start, end),
                        todayPurchaseAmount =
                            repository.getTodayPurchaseAmount(start, end),
                        cashIn =
                            repository.getCashIn(start, end),
                        cashOut =
                            repository.getCashOut(start, end),
                        lowStockCount =
                            repository.getLowStockCount(5.0)
                    )

            } catch (e: Exception) {
                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        error = e.message
                    )
            }
        }
    }

    fun loadMonthlyDashboard() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val zone = ZoneId.systemDefault()
                val now = java.time.YearMonth.now()

                val start =
                    now.atDay(1)
                        .atStartOfDay(zone)
                        .toInstant()
                        .toEpochMilli()

                val end =
                    now.atEndOfMonth()
                        .atTime(23, 59, 59)
                        .atZone(zone)
                        .toInstant()
                        .toEpochMilli()

                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        todaySalesAmount =
                            repository.getTodaySalesAmount(start, end),
                        todaySalesCount =
                            repository.getTodaySalesCount(start, end),
                        todayPurchaseAmount =
                            repository.getTodayPurchaseAmount(start, end),
                        cashIn =
                            repository.getCashIn(start, end),
                        cashOut =
                            repository.getCashOut(start, end),
                        lowStockCount =
                            repository.getLowStockCount(5.0)
                    )

            } catch (e: Exception) {
                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        error = e.message
                    )
            }
        }
    }
}
