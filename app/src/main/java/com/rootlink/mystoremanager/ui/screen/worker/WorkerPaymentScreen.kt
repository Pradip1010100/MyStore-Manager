package com.rootlink.mystoremanager.ui.screen.worker


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.PaymentStatus
import com.rootlink.mystoremanager.data.enums.PaymentType
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.data.enums.WorkerPaymentStatus
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel
import com.rootlink.mystoremanager.util.toReadableDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPaymentScreen(
    navController: NavController,
    viewModel: WorkerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val workerId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("workerId") ?: return

    var amount by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf(PaymentType.SALARY) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var notes by remember { mutableStateOf("") }

    var paymentDate by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    // Load worker once
    LaunchedEffect(workerId) {
        viewModel.loadWorker(workerId)
    }

    val worker = uiState.selectedWorker

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pay Worker") })
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            worker == null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Worker not found")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    /* ================= WORKER INFO ================= */
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(worker.name, style = MaterialTheme.typography.titleMedium)
                            Text("Salary Type: ${worker.salaryType.name}")
                        }
                    }

                    /* ================= PAYMENT TYPE ================= */
                    Text("Payment Type")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentTypeChip(
                            text = "Salary",
                            selected = paymentType == PaymentType.SALARY
                        ) {
                            paymentType = PaymentType.SALARY
                        }

                        PaymentTypeChip(
                            text = "Advance",
                            selected = paymentType == PaymentType.ADVANCE
                        ) {
                            paymentType = PaymentType.ADVANCE
                        }
                    }

                    /* ================= DATE ================= */
                    OutlinedTextField(
                        value = paymentDate.toReadableDate(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Date") },
                        trailingIcon = {
                            IconButton(onClick = {
                                // Hook date picker here later
                            }) {
                                Icon(Icons.Default.Event, contentDescription = "Pick Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    /* ================= WORKED DAYS (MONTHLY ONLY) ================= */
                    if (worker.salaryType == SalaryType.MONTHLY &&
                        paymentType == PaymentType.SALARY
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Worked Days Summary",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))

                                Text(
                                    "Worked days will be calculated from attendance records."
                                )
                            }
                        }
                    }

                    /* ================= AMOUNT ================= */
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    /* ================= PAYMENT MODE ================= */
                    PaymentModeDropdown(
                        selected = paymentMode,
                        onSelected = { paymentMode = it }
                    )

                    /* ================= NOTES ================= */
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    /* ================= SUBMIT ================= */
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (amt != null && amt > 0) {
                                viewModel.payWorker(
                                    WorkerPaymentEntity(
                                        paymentId = 0,
                                        workerId = workerId,
                                        amount = amt,
                                        paymentType = paymentType,
                                        paymentDate = paymentDate,
                                        notes = notes.ifBlank { null },
                                        status = WorkerPaymentStatus.COMPLETED
                                    ),
                                    paymentMode
                                )

                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Submit Payment")
                    }
                }
            }
        }
    }
}


@Composable
private fun PaymentTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentModeDropdown(
    selected: PaymentMode,
    onSelected: (PaymentMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected.name,
            onValueChange = {},
            label = { Text("Payment Mode") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PaymentMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = {
                        onSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}
