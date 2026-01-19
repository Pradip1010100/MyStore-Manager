package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.enums.SupplierStatus
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSupplierScreen(
    navController: NavController
) {
    val viewModel : SupplierViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Supplier") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Supplier Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isEmpty()) return@Button
                    viewModel.addSupplier(
                        SupplierEntity(
                            name = name,
                            phone = phone,
                            address = address,
                            status = SupplierStatus.ACTIVE,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Supplier")
            }
        }
    }
}
