package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rootlink.mystoremanager.data.entity.PersonalTransactionEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.TransactionType
import com.rootlink.mystoremanager.ui.viewmodel.PersonalTransactionViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime

@Composable
fun PersonalTransactionEntryScreen(
    navController: NavHostController,
    viewModel: PersonalTransactionViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()
    var direction by remember { mutableStateOf(TransactionType.OUT) }

    var showPaymentDialog by remember { mutableStateOf(false) }

    if(showPaymentDialog){
        PaymentDialog(
            action = if (direction == TransactionType.OUT) "Pay" else "Receive",
            onConfirm = {amount,title,person, paymentMode,note->
                viewModel.save(
                    amount = amount.toDouble(),
                    title = title,
                    person = person.ifBlank { null },
                    direction = direction,
                    paymentMode = paymentMode,
                    note = note.ifBlank { null }
                )
                showPaymentDialog = false
            },
            onDismiss = {
                showPaymentDialog = false
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------------- HEADER ---------------- */

        Text(
            text = "Personal Money",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(12.dp))

        //Action Buttons
        Row(
            modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    direction = TransactionType.OUT
                    showPaymentDialog = true
                }
            ) {
                Text("Pay")
            }
            Button(
                onClick = {
                    direction = TransactionType.IN
                    showPaymentDialog = true
                }
            ) {
                Text("Receive")
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ---------------- HISTORY ---------------- */

        Text(
            text = "History",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text("No personal transactions yet")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(history) { tx ->
                    PersonalTransactionItem(tx)
                }
            }
        }
    }
}

/*---------------------------------
PayRecive Dialog
-----------------------------------*/

@Composable
fun PaymentDialog(
    action:String,
    onDismiss : ()->Unit,
    onConfirm : (String, String, String, PaymentMode, String)->Unit,

    ){
    val presetReasons = listOf(
        "Rent", "Medical", "Grocery", "Loan", "Gift", "Travel", "Other"
    )
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var person by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        title = {Text(action)},
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(Modifier.height(12.dp))
                /* ---------------- ENTRY FORM ---------------- */

                Text(
                    text = "Payment Mode",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(6.dp))

                LazyRow {
                    items(PaymentMode.entries) { mode ->
                        FilterChip(
                            selected = paymentMode == mode,
                            onClick = { paymentMode = mode },
                            label = { Text(mode.name) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Select Reason",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(6.dp))

                LazyRow {
                    items(presetReasons) { reason ->
                        FilterChip(
                            selected = title == reason,
                            onClick = { title = reason },
                            label = { Text(reason) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = person,
                    onValueChange = { person = it },
                    label = { Text("Person (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (amount.isNotBlank() && title.isNotBlank()) {
                        onConfirm(amount,title,person, paymentMode,note)
                        amount = ""
                        title = ""
                        person = ""
                        note = ""
                    }
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        },
    )
}

/* ---------------------------------------------------
   HISTORY ITEM
--------------------------------------------------- */

@Composable
fun PersonalTransactionItem(tx: PersonalTransactionEntity) {
    val isIn = tx.direction == TransactionType.IN

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isIn) Color(0xFFE8F5E9)
                else Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            /* ---------- TITLE + AMOUNT ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tx.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = (if (isIn) "+ ₹" else "- ₹") + tx.amount,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isIn) Color(0xFF2E7D32)
                        else Color(0xFFC62828)
                )
            }

            /* ---------- PAY / RECEIVE CHIP ---------- */
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        if (isIn) "Received"
                        else "Paid"
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor =
                        if (isIn) Color(0xFF2E7D32)
                        else Color(0xFFC62828),
                    labelColor = Color.White
                )
            )

            /* ---------- PERSON ---------- */
            tx.personName?.takeIf { it.isNotBlank() }?.let {
                InfoRow(label = "Person", value = it)
            }

            /* ---------- PAYMENT MODE ---------- */
            InfoRow(
                label = "Mode",
                value = tx.paymentMode.name
            )

            /* ---------- NOTE ---------- */
            tx.note?.takeIf { it.isNotBlank() }?.let {
                InfoRow(label = "Note", value = it)
            }

            /* ---------- DATE ---------- */
            InfoRow(
                label = "Date",
                value = tx.date.toReadableDateTime()
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 12.sp
        )
    }
}
