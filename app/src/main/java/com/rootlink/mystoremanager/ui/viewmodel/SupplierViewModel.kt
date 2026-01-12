package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.dao.SupplierDao
import kotlinx.coroutines.launch

class SupplierViewModel(
    private val supplierDao: SupplierDao
) : ViewModel() {

    fun loadSuppliers() {
        viewModelScope.launch {
            supplierDao.getActive()
        }
    }
}