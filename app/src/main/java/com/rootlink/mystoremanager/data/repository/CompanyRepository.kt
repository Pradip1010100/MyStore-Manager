package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.CompanyProfileDao
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import jakarta.inject.Inject

class CompanyRepository @Inject constructor(
    private val dao: CompanyProfileDao
) {
    suspend fun getCompany() = dao.get()
    suspend fun saveCompany(profile: CompanyProfileEntity) = dao.save(profile)
}
