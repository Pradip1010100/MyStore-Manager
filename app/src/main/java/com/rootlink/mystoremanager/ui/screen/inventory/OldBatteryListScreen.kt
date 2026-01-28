package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldBatteryListScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val batteries by viewModel.oldBatteries.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingBattery by remember { mutableStateOf<OldBatteryEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadOldBatteries()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Old Batteries") },
                actions = {
                    TextButton(
                        onClick = {
                            editingBattery = null
                            showDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add")
                    }
                }
            )
        }
    ) { padding ->

        if (batteries.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No old batteries recorded",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(batteries) { battery ->
                    OldBatteryRow(
                        battery = battery,
                        onEdit = {
                            editingBattery = battery
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        OldBatteryDialog(
            battery = editingBattery,
            onSave = {
                if (editingBattery == null) {
                    viewModel.addOldBattery(it)
                } else {
                    viewModel.updateOldBattery(it)
                }
                showDialog = false
                editingBattery = null
            },
            onDismiss = {
                showDialog = false
                editingBattery = null
            }
        )
    }
}

/* -------------------------------------------------------------------------- */
/* ROW */
/* -------------------------------------------------------------------------- */
@Composable
private fun OldBatteryRow(
    battery: OldBatteryEntity,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text =
                        if (battery.saleId != null)
                            "Linked to Sale #${battery.saleId}"
                        else
                            "Direct Entry",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "${battery.name} • ${battery.brand}",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    "Type: ${battery.batteryType}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "Quantity: ${battery.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Rate: ₹${battery.rate}",
                    style = MaterialTheme.typography.bodySmall
                )

                battery.weight?.let {
                    Text(
                        "Weight: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                battery.note?.let {
                    Text(
                        "Note: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    "Amount: ₹%.2f".format(battery.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                enabled = battery.saleId == null, // 🔒 lock sale-linked entries
                onClick = onEdit
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* DIALOG */
/* -------------------------------------------------------------------------- */

@Composable
fun OldBatteryDialog(
    battery: OldBatteryEntity?,
    onSave: (OldBatteryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(battery?.name ?: "") }
    var brand by remember { mutableStateOf(battery?.brand ?: "") }
    var type by remember { mutableStateOf(battery?.batteryType ?: "") }

    var qty by remember { mutableStateOf(battery?.quantity?.toString() ?: "") }
    var rate by remember { mutableStateOf(battery?.rate?.toString() ?: "") }
    var weight by remember { mutableStateOf(battery?.weight?.toString() ?: "") }
    var note by remember { mutableStateOf(battery?.note ?: "") }

    val quantity = qty.toIntOrNull() ?: 0
    val rateValue = rate.toDoubleOrNull() ?: 0.0
    val weightValue = weight.toDoubleOrNull()

    val amount = quantity * rateValue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (battery == null)
                    "Add Old Battery"
                else
                    "Edit Old Battery"
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                if (battery?.saleId != null) {
                    Text(
                        "Linked to Sale #${battery.saleId}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Battery Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Battery Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate (per battery)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Divider()

                Text(
                    "Amount: ₹%.2f".format(amount),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            Button(
                enabled =
                    name.isNotBlank() &&
                            brand.isNotBlank() &&
                            type.isNotBlank() &&
                            quantity > 0 &&
                            rateValue > 0,
                onClick = {
                    onSave(
                        OldBatteryEntity(
                            oldBatteryId = battery?.oldBatteryId ?: 0L,
                            saleId = battery?.saleId, // preserved
                            name = name.trim(),
                            brand = brand.trim(),
                            batteryType = type.trim(),
                            quantity = quantity,
                            rate = rateValue,
                            weight = weightValue,
                            note = note.trim().ifBlank { null }
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
