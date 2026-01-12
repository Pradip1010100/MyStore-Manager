package com.rootlink.mystoremanager.ui.screen.worker


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.enums.PaymentMode
import com.rootlink.mystoremanager.data.entity.enums.PaymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPaymentScreen(
    navController: NavController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val workerId = backStackEntry
        ?.arguments
        ?.getLong("workerId") ?: return

    var amount by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf(PaymentType.SALARY) }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay Worker") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Payment Type
            Text(
                text = "Payment Type",
                style = MaterialTheme.typography.titleSmall
            )

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

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Payment Mode
            Text(
                text = "Payment Mode",
                style = MaterialTheme.typography.titleSmall
            )

            PaymentModeDropdown(
                selected = paymentMode,
                onSelected = { paymentMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (amount.isNotBlank()) {
                        // TODO: call WorkerViewModel.payWorker(...)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Payment")
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
