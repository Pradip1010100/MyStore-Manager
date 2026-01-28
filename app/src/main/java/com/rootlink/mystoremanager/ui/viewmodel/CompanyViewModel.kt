package com.rootlink.mystoremanager.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import com.rootlink.mystoremanager.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _company = MutableStateFlow<CompanyProfileEntity?>(null)
    val company: StateFlow<CompanyProfileEntity?> = _company
    companion object {
        private const val DB_NAME = "myStoreManager.db"
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

    suspend fun createBackupFile(context: Context): File =
        withContext(Dispatchers.IO) {

            val dbDir = context.getDatabasePath(DB_NAME).parentFile
                ?: throw IllegalStateException("Database directory not found")

            val mainDb = File(dbDir, DB_NAME)
            val wal = File(dbDir, "$DB_NAME-wal")
            val shm = File(dbDir, "$DB_NAME-shm")

            require(mainDb.exists()) { "Main database file missing" }

            val backupDir = File(context.getExternalFilesDir(null), "backup")
            if (!backupDir.exists()) backupDir.mkdirs()

            val today = SimpleDateFormat(
                "dd_MM_yyyy",
                Locale.getDefault()
            ).format(Date())

            val backupFile = File(
                backupDir,
                "myStoreManager_${today}.db"
            )

            FileOutputStream(backupFile).use { out ->
                mainDb.inputStream().use { it.copyTo(out) }
                if (wal.exists()) wal.inputStream().use { it.copyTo(out) }
                if (shm.exists()) shm.inputStream().use { it.copyTo(out) }
            }

            backupFile
        }

    suspend fun restoreFromUri(
        context: Context,
        uri: Uri
    ) = withContext(Dispatchers.IO) {

        val dbFile = context.getDatabasePath(DB_NAME)
        val walFile = File(dbFile.path + "-wal")
        val shmFile = File(dbFile.path + "-shm")

        // 1️⃣ DELETE OLD FILES
        if (dbFile.exists()) dbFile.delete()
        if (walFile.exists()) walFile.delete()
        if (shmFile.exists()) shmFile.delete()

        // 2️⃣ RESTORE MAIN DB FILE
        context.contentResolver.openInputStream(uri)?.use { input ->
            dbFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Unable to read backup file")
    }

}
