package com.rootlink.mystoremanager.ui.screen.dashboard

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import com.rootlink.mystoremanager.ui.viewmodel.CompanyViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    navController: NavHostController,
    viewModel: CompanyViewModel = hiltViewModel()
) {
    val company by viewModel.company.collectAsState()
    val context = navController.context
    val scope = rememberCoroutineScope()

    var showEdit by remember { mutableStateOf(false) }
    var showRestoreWarning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCompany()
    }

    /* ---------------- RESTORE PICKER ---------------- */

    val restoreLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                scope.launch {
                    viewModel.restoreFromUri(context, it)

                    Toast.makeText(
                        context,
                        "Restore complete. Restarting app…",
                        Toast.LENGTH_LONG
                    ).show()

                    android.os.Handler(android.os.Looper.getMainLooper())
                        .postDelayed({
                            exitProcess(0)
                        }, 1500)
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Company Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showEdit = true }) {
                        Icon(Icons.Default.Edit, null)
                    }
                }
            )
        }
    ) { padding ->

        company?.let {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ProfileRow("Company Name", it.name)
                ProfileRow("Business Type", it.businessType)
                ProfileRow("Phone", it.phone)
                ProfileRow("Address", it.address)

                Divider()
                Text("Data", style = MaterialTheme.typography.titleSmall)

                /* ---------------- BACKUP ---------------- */

                ListItem(
                    headlineContent = { Text("Backup Data") },
                    leadingContent = { Icon(Icons.Default.CloudUpload, null) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            try {
                                val file = viewModel.createBackupFile(context)

                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/octet-stream"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }

                                context.startActivity(
                                    Intent.createChooser(intent, "Backup Database")
                                )

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Backup failed: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )

                /* ---------------- RESTORE ---------------- */

                ListItem(
                    headlineContent = { Text("Restore Data") },
                    leadingContent = { Icon(Icons.Default.CloudDownload, null) },
                    modifier = Modifier.clickable {
                        showRestoreWarning = true
                    }
                )

                Divider()
                Text("More", style = MaterialTheme.typography.titleSmall)
                ListItem(
                    headlineContent = {
                        Text("About App")
                    },
                    leadingContent = {
                        Icon(Icons.Default.Info, null)
                    }
                )
                ListItem(
                    headlineContent = {
                        Text("Privacy Policy")
                        },
                    leadingContent = {
                        Icon(Icons.Default.PrivacyTip, null) }
                )
            }
        }
    }

    /* ---------------- RESTORE WARNING ---------------- */

    if (showRestoreWarning) {
        AlertDialog(
            onDismissRequest = { showRestoreWarning = false },
            icon = { Icon(Icons.Default.Warning, null) },
            title = { Text("Restore Backup") },
            text = {
                Text(
                    "This will replace ALL current data.\n\n" +
                            "This action cannot be undone.\n\nContinue?"
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        showRestoreWarning = false
                        restoreLauncher.launch(arrayOf("*/*"))
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreWarning = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEdit) {
        EditCompanyDialog(
            company = company,
            onSave = {
                viewModel.updateCompany(it)
                showEdit = false
            },
            onDismiss = { showEdit = false }
        )
    }
}


/* ---------------- EDIT COMPANY ---------------- */

@Composable
fun EditCompanyDialog(
    company: CompanyProfileEntity?,
    onSave: (CompanyProfileEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var type by remember { mutableStateOf(company?.businessType ?: "") }
    var phone by remember { mutableStateOf(company?.phone ?: "") }
    var address by remember { mutableStateOf(company?.address ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Company Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Company Name") })
                OutlinedTextField(type, { type = it }, label = { Text("Business Type") })
                OutlinedTextField(
                    phone,
                    { phone = it },
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(address, { address = it }, label = { Text("Address") })
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() && phone.isNotBlank(),
                onClick = {
                    onSave(
                        CompanyProfileEntity(
                            id = 1,
                            name = name.trim(),
                            businessType = type.trim(),
                            phone = phone.trim(),
                            address = address.trim()
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/* ---------------- PROFILE ROW ---------------- */

@Composable
fun ProfileRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
