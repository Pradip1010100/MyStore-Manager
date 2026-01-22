package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rootlink.mystoremanager.ui.viewmodel.CompanyViewModel

@Composable
fun CompanySetupScreen(
    onDone: () -> Unit
) {
    val viewModel : CompanyViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Business Details", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Business Name") })
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Business Type") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveCompany(name, type, address, phone)
                onDone()
            },
            enabled = name.isNotBlank()
        ) {
            Text("Save")
        }
    }
}
