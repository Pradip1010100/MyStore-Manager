package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.data.enums.WorkType
import com.rootlink.mystoremanager.data.enums.WorkerStatus
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkerScreen(
    navController: NavController,
    viewModel: WorkerViewModel = hiltViewModel()
) {

    /* -------------------------
       STATE
     ------------------------- */
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var team by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    var workType by remember { mutableStateOf(WorkType.OTHER) }
    var salaryType by remember { mutableStateOf(SalaryType.DAILY) }
    var salaryAmount by remember { mutableStateOf("") }

    var status by remember { mutableStateOf(WorkerStatus.ACTIVE) }

    var dob by remember { mutableStateOf<Long?>(null) }
    var joinDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var leaveDate by remember { mutableStateOf<Long?>(null) }

    var notes by remember { mutableStateOf("") }

    /* -------------------------
       UI
     ------------------------- */
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Worker") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* -------------------------
               PROFILE PHOTO (PLACEHOLDER)
             ------------------------- */
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            /* -------------------------
               BASIC INFO
             ------------------------- */
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            /* -------------------------
               TEAM & POSITION
             ------------------------- */
            val teamOptions = listOf(
                "Sales",
                "Service",
                "Warehouse",
                "Accounts"
            )

            EditableDropdownField(
                label = "Team / Department",
                value = team,
                options = teamOptions,
                onValueChange = { team = it }
            )


            val positionOptions = listOf(
                "Technician",
                "Sales Executive",
                "Helper",
                "Manager"
            )

            EditableDropdownField(
                label = "Position / Role",
                value = position,
                options = positionOptions,
                onValueChange = { position = it }
            )



            /*---------------------
               SALARY TYPE & AMOUNT
             ------------------------- */
            Text("Salary Type", style = MaterialTheme.typography.titleSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SalaryType.entries.forEach {
                    FilterChip(
                        selected = salaryType == it,
                        onClick = { salaryType = it },
                        label = { Text(it.name) }
                    )
                }
            }

            OutlinedTextField(
                value = salaryAmount,
                onValueChange = { salaryAmount = it },
                label = {
                    Text(
                        when (salaryType) {
                            SalaryType.DAILY -> "Daily Salary Amount"
                            SalaryType.MONTHLY -> "Monthly Salary Amount"
                            SalaryType.PER_JOB -> "Per Job Salary Amount"
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            /* -------------------------
               STATUS
             ------------------------- */
            Text("Employment Status", style = MaterialTheme.typography.titleSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkerStatus.entries.forEach {
                    FilterChip(
                        selected = status == it,
                        onClick = { status = it },
                        label = { Text(it.name) }
                    )
                }
            }

            /* -------------------------
               NOTES
             ------------------------- */
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            /* -------------------------
               SAVE
             ------------------------- */
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val amount = salaryAmount.toDoubleOrNull() ?: return@Button
                    if (name.isBlank()) return@Button

                    viewModel.addWorker(
                        WorkerEntity(
                            workerId = 0,
                            name = name.trim(),
                            phone = phone.ifBlank { null },
                            email = email.ifBlank { null },
                            address = address.ifBlank { null },
                            team = team.ifBlank { null },
                            position = position.ifBlank { null },
                            salaryType = salaryType,
                            salaryAmount = amount,
                            defaultRate = amount,
                            profileImageUri = null,
                            status = status,
                            joinedAt = joinDate,
                            leftAt = leaveDate,
                            dob = dob,
                            notes = notes
                        )
                    )

                    navController.popBackStack()
                }
            ) {
                Text("Save Worker")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange, // 👈 USER CAN TYPE
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
