package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.WorkerRepository
import kotlinx.coroutines.launch

class WorkerViewModel(
    private val workerRepository: WorkerRepository
) : ViewModel() {

    fun loadActiveWorkers() {
        viewModelScope.launch {
            workerRepository.getActiveWorkers()
        }
    }

    fun payWorker(/* params later */) {
        viewModelScope.launch {
            // workerRepository.payWorker(...)
        }
    }
}