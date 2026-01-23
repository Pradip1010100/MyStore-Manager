package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.PaymentType
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.data.enums.WorkerPaymentStatus
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel
import com.rootlink.mystoremanager.util.toReadableDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPaymentScreen(
    navController: NavController,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val workerId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("workerId") ?: return

    LaunchedEffect(workerId) {
        viewModel.loadWorker(workerId)
    }

    val worker = uiState.selectedWorker ?: return

    /* ---------------- STATE ---------------- */

    var paymentType by remember { mutableStateOf(PaymentType.SALARY) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }

    var fromDate by remember { mutableStateOf<Long?>(null) }
    var toDate by remember { mutableStateOf<Long?>(null) }

    var manualAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pay Worker") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ---------------- WORKER INFO ---------------- */

            Card {
                Column(Modifier.padding(16.dp)) {
                    Text(worker.name, style = MaterialTheme.typography.titleMedium)
                    Text("Salary Type: ${worker.salaryType}")
                }
            }

            /* ---------------- PAYMENT TYPE ---------------- */

            Text("Payment Type", fontWeight = FontWeight.Medium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentTypeChip("Salary", paymentType == PaymentType.SALARY) {
                    paymentType = PaymentType.SALARY
                }
                PaymentTypeChip("Advance", paymentType == PaymentType.ADVANCE) {
                    paymentType = PaymentType.ADVANCE
                }
                PaymentTypeChip("Other", paymentType == PaymentType.OTHER) {
                    paymentType = PaymentType.OTHER
                }
            }

            /* ---------------- SALARY PREVIEW (READ ONLY) ---------------- */

            if (paymentType == PaymentType.SALARY) {

                when (worker.salaryType) {

                    SalaryType.DAILY -> {
                        DatePickerField(
                            label = "Select Date",
                            date = fromDate
                        ) {
                            fromDate = it
                            toDate = it
                            viewModel.calculateSalary(workerId, it, it)
                        }
                    }

                    SalaryType.MONTHLY -> {
                        MonthPicker { month ->
                            val start =
                                month.atDay(1)
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()

                            val end =
                                month.atEndOfMonth()
                                    .atTime(23, 59, 59)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()

                            fromDate = start
                            toDate = end
                            viewModel.calculateSalary(workerId, start, end)
                        }
                    }

                    SalaryType.PER_JOB -> Unit
                }

                uiState.salaryPreview?.let { preview ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "Attendance Summary",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(Modifier.height(8.dp))

                            Text("Present Days: ${preview.presentDays}")
                            Text("Total Days: ${preview.totalDays}")

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Estimated Salary: ₹%.2f"
                                    .format(preview.estimatedSalary),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /* ---------------- MANUAL PAYMENT AMOUNT ---------------- */

            OutlinedTextField(
                value = manualAmount,
                onValueChange = { manualAmount = it },
                label = { Text("Payment Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            /* ---------------- PAYMENT MODE ---------------- */

            PaymentModeDropdown(
                selected = paymentMode,
                onSelected = { paymentMode = it }
            )

            /* ---------------- PAY ---------------- */

            val payAmount = manualAmount.toDoubleOrNull()

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = payAmount != null && payAmount > 0,
                onClick = {
                    viewModel.payWorker(
                        WorkerPaymentEntity(
                            paymentId = 0,
                            workerId = workerId,
                            paymentDate = System.currentTimeMillis(),
                            paymentType = paymentType,
                            amount = payAmount ?: 0.0,
                            status = WorkerPaymentStatus.COMPLETED,
                            notes = null
                        ),
                        paymentMode
                    )
                    navController.popBackStack()
                }
            ) {
                Text("Pay ₹%.2f".format(payAmount ?: 0.0))
            }
        }
    }
}


/* ========================================================================== */
/*                               DATE PICKER                                  */
/* ========================================================================== */

@Composable
private fun DatePickerField(
    label: String,
    date: Long?,
    onDateSelected: (Long) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date?.toReadableDate() ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.Event, null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showPicker) {
        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let(onDateSelected)
                    showPicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

/* ========================================================================== */
/*                               MONTH PICKER                                 */
/* ========================================================================== */

@Composable
private fun MonthPicker(
    onMonthSelected: (YearMonth) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(YearMonth.now()) }

    OutlinedTextField(
        value =
            "${selected.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${selected.year}",
        onValueChange = {},
        readOnly = true,
        label = { Text("Select Month") },
        trailingIcon = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Event, null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
        (0..11).forEach {
            val month = YearMonth.now().minusMonths(it.toLong())
            DropdownMenuItem(
                text = {
                    Text(
                        "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}"
                    )
                },
                onClick = {
                    selected = month
                    expanded = false
                    onMonthSelected(month)
                }
            )
        }
    }
}

/* ========================================================================== */
/*                           PAYMENT MODE DROPDOWN                             */
/* ========================================================================== */

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
            PaymentMode.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* ========================================================================== */
/*                               CHIP                                         */
/* ========================================================================== */

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
