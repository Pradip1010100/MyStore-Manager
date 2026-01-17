package com.rootlink.mystoremanager.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.dao.SupplierDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierDao: SupplierDao
) : ViewModel() {

    fun loadSuppliers() {
        viewModelScope.launch {
            supplierDao.getActive()
        }
    }
}