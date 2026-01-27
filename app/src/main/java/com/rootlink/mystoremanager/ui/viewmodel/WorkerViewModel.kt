package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.WorkerAttendanceEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.AttendanceStatus
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.repository.WorkerRepository
import com.rootlink.mystoremanager.ui.viewmodel.state.AttendanceUiItem
import com.rootlink.mystoremanager.ui.viewmodel.state.WorkerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val workerRepository: WorkerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerUiState())
    val uiState: StateFlow<WorkerUiState> = _uiState.asStateFlow()

    fun addWorker(worker: WorkerEntity) {
        viewModelScope.launch {
            workerRepository.addWorker(worker)
            loadWorkers()
        }
    }

    fun loadAttendance(date: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val workers = workerRepository.getActiveWorkers()
                val existing = workerRepository.getAttendanceForDate(date)
                val map = existing.associateBy { it.workerId }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    attendance = workers.map { worker ->
                        AttendanceUiItem(
                            workerId = worker.workerId,
                            workerName = worker.name,
                            status = map[worker.workerId]?.status
                                ?: AttendanceStatus.UNMARKED
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateAttendance(
        workerId: Long,
        status: AttendanceStatus
    ) {
        val updated = _uiState.value.attendance.map {
            if (it.workerId == workerId) it.copy(status = status) else it
        }
        _uiState.value = _uiState.value.copy(attendance = updated)
    }

    fun saveAttendance(date: Long) {
        viewModelScope.launch {
            _uiState.value.attendance.forEach {
                workerRepository.markAttendance(
                    WorkerAttendanceEntity(
                        workerId = it.workerId,
                        date = date,
                        status = it.status
                    )
                )
            }
        }
    }

    fun loadWorkerAttendanceForMonth(
        workerId: Long,
        month: YearMonth
    ) {
        viewModelScope.launch {
            try {
                val start = month
                    .atDay(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val end = month
                    .atEndOfMonth()
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val records =
                    workerRepository.getAttendanceForWorkerBetween(
                        workerId,
                        start,
                        end
                    )

                val map = records.associate {
                    it.date to it.status
                }

                _uiState.value = _uiState.value.copy(
                    monthlyAttendance = map
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }


    // -------------------------
    // LOAD WORKERS
    // -------------------------
    fun loadWorkers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val workers = workerRepository.getAllWorkers()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workers = workers,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateWorker(worker: WorkerEntity) {
        viewModelScope.launch {
            workerRepository.updateWorker(worker)
            loadWorker(worker.workerId)
        }
    }


    fun loadWorker(workerId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val worker = workerRepository.getWorkerById(workerId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedWorker = worker,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // -------------------------
    // PAY WORKER
    // -------------------------
    fun payWorker(
        payment: WorkerPaymentEntity,
        paymentMode: PaymentMode
    ) {
        viewModelScope.launch {
            try {
                workerRepository.payWorker(payment, paymentMode)

                // 🔥 IMPORTANT
                loadWorkerLedger(payment.workerId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun previewSalary(
        workerId: Long,
        from: Long,
        to: Long
    ) {
        viewModelScope.launch {
            val worker = workerRepository.getWorkerById(workerId)
            val salary =
                workerRepository.calculateSalary(worker, from, to)

            _uiState.value =
                _uiState.value.copy(calculatedSalary = salary)
        }
    }

    // -------------------------
    // LOAD LEDGER
    // -------------------------
    fun loadWorkerLedger(workerId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val payments =
                    workerRepository.getPaymentsForWorker(workerId)

                _uiState.value =
                    _uiState.value.copy(
                        payments = payments,
                        isLoading = false
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
            }
        }
    }

    fun deactivateWorker(workerId: Long) {
        viewModelScope.launch {
            workerRepository.deactivateWorker(workerId)
            loadWorkers()
        }
    }

    fun activateWorker(workerId: Long) {
        viewModelScope.launch {
            workerRepository.activateWorker(workerId)
            loadWorkers()
        }
    }

    fun calculateSalary(
        workerId: Long,
        from: Long,
        to: Long
    ) {
        viewModelScope.launch {
            val worker = workerRepository.getWorkerById(workerId)
            val preview =
                workerRepository.calculateSalaryPreview(worker, from, to)

            _uiState.value =
                _uiState.value.copy(
                    salaryPreview = preview
                )
        }
    }


    fun loadWorkerSummary(
        workerId: Long,
        from: Long,
        to: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val worker = workerRepository.getWorkerById(workerId)
                val (salary, paid) =
                    workerRepository.getWorkerBalance(workerId, from, to)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedWorker = worker,
                    calculatedSalary = salary,
                    paidAmount = paid,
                    balance = salary - paid
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun generateSalary(
        workerId: Long,
        from: Long,
        to: Long,
        paymentMode: PaymentMode
    ) {
        viewModelScope.launch {
            workerRepository.generateSalary(
                workerId,
                from,
                to,
                paymentMode
            )
        }
    }

}
