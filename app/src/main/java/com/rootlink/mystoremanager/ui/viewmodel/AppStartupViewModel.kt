package com.rootlink.mystoremanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.repository.CompanyRepository
import com.rootlink.mystoremanager.data.repository.SalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppStartupViewModel @Inject constructor(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    private val _isCompanySetup = MutableStateFlow<Boolean?>(null)
    val isCompanySetup: StateFlow<Boolean?> = _isCompanySetup

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isCompanySetup.value =
                companyRepository.getCompany() != null
        }
    }

}
