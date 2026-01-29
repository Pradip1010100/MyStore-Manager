package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import com.rootlink.mystoremanager.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _company = MutableStateFlow<CompanyProfileEntity?>(null)
    val company: StateFlow<CompanyProfileEntity?> = _company
    companion object {
        private const val DB_NAME = "myStoreManager"
    }
    fun loadCompany() {
        viewModelScope.launch {
            _company.value = repository.getCompany()
        }
    }

    fun updateCompany(company: CompanyProfileEntity) {
        viewModelScope.launch {
            repository.saveCompany(company)
            _company.value = company
        }
    }

    fun saveCompany(
        name: String,
        type: String,
        address: String,
        phone: String
    ) {
        viewModelScope.launch {
            repository.saveCompany(
                CompanyProfileEntity(
                    name = name,
                    businessType = type,
                    address = address,
                    phone = phone
                )
            )
            loadCompany()
        }
    }

}
