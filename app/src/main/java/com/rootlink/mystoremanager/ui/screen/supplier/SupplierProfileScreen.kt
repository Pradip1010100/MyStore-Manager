package com.rootlink.mystoremanager.ui.screen.supplier

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.R
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.enums.SupplierStatus
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierProfileScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()
    val context = LocalContext.current

    val supplierId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("supplierId")
            ?: return

    val supplier by viewModel.supplier.collectAsState()
    val totals by viewModel.totals.collectAsState()

    var showEdit by remember { mutableStateOf(false) }

    LaunchedEffect(supplierId) {
        viewModel.loadSupplierProfile(supplierId)
    }

    val s = supplier ?: return
    val t = totals ?: return
    val phone = s.phone?.let(::normalizeIndianPhone)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplier Profile") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            /* ---------- HEADER ---------- */
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        SupplierAvatar(s.name)

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = s.name,
                                style = MaterialTheme.typography.titleLarge
                            )

                            if (phone != null) {
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        StatusChip(s.status)
                    }

                    if (!s.address.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = s.address,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- QUICK ACTIONS ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                ActionIcon(
                    label = "Call",
                    icon = Icons.Default.Call,
                    enabled = phone != null
                ) {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    )
                }

                ActionIcon(
                    label = "WhatsApp",
                    painter = painterResource(R.drawable.ic_whatsapp),
                    enabled = phone != null
                ) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/${phone!!.replace("+", "")}")
                        )
                    )
                }

                ActionIcon(
                    label = "Edit",
                    icon = Icons.Default.Edit
                ) {
                    showEdit = true
                }
            }

            Spacer(Modifier.height(20.dp))

            /* ---------- FINANCIAL SUMMARY ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FinanceCard(
                    title = "Purchased",
                    amount = t.totalPurchased
                )

                FinanceCard(
                    title = "Paid",
                    amount = t.totalPaid,
                    color = MaterialTheme.colorScheme.primary
                )

                FinanceCard(
                    title = "Due",
                    amount = t.due,
                    color =
                        if (t.due > 0)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- MANAGEMENT ---------- */
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.deactivateSupplier(supplierId)
                    navController.popBackStack()
                }
            ) {
                Text("Deactivate Supplier")
            }
        }
    }

    if (showEdit) {
        EditSupplierDialog(
            supplier = s,
            onSave = {
                viewModel.updateSupplier(
                    it.copy(phone = normalizeIndianPhone(it.phone))
                )
                showEdit = false
            },
            onDismiss = { showEdit = false }
        )
    }
}

/* ===================== HELPERS ===================== */

private fun normalizeIndianPhone(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return when {
        digits.length == 10 -> "+91$digits"
        digits.length == 12 && digits.startsWith("91") -> "+$digits"
        digits.startsWith("+91") -> digits
        else -> "+91$digits"
    }
}
@Composable
fun EditSupplierDialog(
    supplier: SupplierEntity,
    onSave: (SupplierEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(supplier.name) }
    var phone by remember { mutableStateOf(supplier.phone ?: "") }
    var address by remember { mutableStateOf(supplier.address ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Supplier") },
        text = {
            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Supplier Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        supplier.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            address = address.trim()
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

@Composable
fun SupplierAvatar(
    name: String,
    size: Dp = 48.dp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"

    Card(
        shape = CircleShape,
        modifier = Modifier.size(size),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                textAlign = TextAlign.Center,
                fontSize = (size.value * 0.45f).sp,
                lineHeight = (size.value * 0.45f).sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ActionIcon(
    label: String,
    icon: ImageVector? = null,
    painter: Painter? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        IconButton(enabled = enabled, onClick = onClick) {
            when {
                icon != null ->
                    Icon(icon, contentDescription = label)
                painter != null ->
                    Icon(
                        painter = painter,
                        contentDescription = label,
                        tint = Color.Unspecified
                    )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun RowScope.FinanceCard(
    title: String,
    amount: Double,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "₹${"%,.0f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
fun StatusChip(status: SupplierStatus) {
    val color =
        if (status == SupplierStatus.ACTIVE)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.error

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
