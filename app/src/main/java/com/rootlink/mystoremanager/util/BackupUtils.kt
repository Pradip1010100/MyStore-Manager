package com.rootlink.mystoremanager.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupUtils {

    private const val DB_NAME = "myStoreManager"

    /* ===================== BACKUP ===================== */

    fun backupDatabase(
        context: Context,
        uri: Uri,
        onProgress: (Boolean) -> Unit = {}
    ) {
        try {
            onProgress(true)

            val dbFile = context.getDatabasePath(DB_NAME)
            val walFile = File(dbFile.path + "-wal")
            val shmFile = File(dbFile.path + "-shm")

            val out = context.contentResolver.openOutputStream(uri)
                ?: throw Exception("Output stream is null")

            ZipOutputStream(out).use { zip ->

                zip.putNextEntry(ZipEntry(DB_NAME))
                FileInputStream(dbFile).copyTo(zip)
                zip.closeEntry()

                if (walFile.exists()) {
                    zip.putNextEntry(ZipEntry("$DB_NAME-wal"))
                    FileInputStream(walFile).copyTo(zip)
                    zip.closeEntry()
                }

                if (shmFile.exists()) {
                    zip.putNextEntry(ZipEntry("$DB_NAME-shm"))
                    FileInputStream(shmFile).copyTo(zip)
                    zip.closeEntry()
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            onProgress(false)
        }
    }

    /* ===================== RESTORE ===================== */

    fun restoreDatabase(
        context: Context,
        uri: Uri,
        onComplete: () -> Unit = {}
    ) {
        try {
            val dbFile = context.getDatabasePath(DB_NAME)
            val walFile = File(dbFile.path + "-wal")
            val shmFile = File(dbFile.path + "-shm")
            var hasDb = false

            // 🔴 REQUIRED: delete old files
            dbFile.delete()
            walFile.delete()
            shmFile.delete()

            val input = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Input stream is null")

            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                dbFile.parentFile?.mkdirs()
                while (entry != null) {
                    if (entry.name == DB_NAME) hasDb = true
                    val outFile = when (entry.name) {
                        DB_NAME -> dbFile
                        "$DB_NAME-wal" -> walFile
                        "$DB_NAME-shm" -> shmFile
                        else -> null
                    }

                    outFile?.let {
                        FileOutputStream(it).use { fos ->
                            zip.copyTo(fos)
                        }
                    }

                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
            require(hasDb) { "Invalid backup file" }
            onComplete()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
