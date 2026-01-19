package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SupplierPaymentEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaySupplierScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()

    val supplierId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("supplierId") ?: return

    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pay Supplier") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.paySupplier(
                        SupplierPaymentEntity(
                            supplierId = supplierId,
                            paymentDate = System.currentTimeMillis(),
                            amount = amount.toDouble(),
                            paymentMode = PaymentMode.CASH,
                            notes = notes
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay")
            }
        }
    }
}
