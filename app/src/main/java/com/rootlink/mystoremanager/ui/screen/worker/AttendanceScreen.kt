package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.enums.AttendanceStatus
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel
import com.rootlink.mystoremanager.ui.viewmodel.state.AttendanceUiItem
import com.rootlink.mystoremanager.util.toReadableDate
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    navController: NavController,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val todayMillis = remember {
        LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    LaunchedEffect(Unit) {
        viewModel.loadAttendance(todayMillis)
    }

    // ---- SUMMARY ----
    val presentCount = uiState.attendance.count {
        it.status == AttendanceStatus.PRESENT
    }
    val absentCount = uiState.attendance.count {
        it.status == AttendanceStatus.ABSENT
    }
    val unmarkedCount = uiState.attendance.count {
        it.status == AttendanceStatus.UNMARKED
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mark Attendance") })
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

            uiState.attendance.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No workers found")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        item {
                            DateHeader(todayMillis)
                        }

                        item {
                            AttendanceSummary(
                                present = presentCount,
                                absent = absentCount
                            )
                        }

                        items(uiState.attendance) { item ->
                            AttendanceRowAdvanced(
                                item = item,
                                onStatusChange = {
                                    viewModel.updateAttendance(
                                        item.workerId,
                                        it
                                    )
                                }
                            )
                        }
                    }

                    // ⚠️ WARNING
                    if (unmarkedCount > 0) {
                        Text(
                            text = "⚠ Attendance not fully marked",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // ✅ SAVE BUTTON (NOT FAB)
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        enabled = unmarkedCount == 0,
                        onClick = {
                            viewModel.saveAttendance(todayMillis)
                            navController.popBackStack()
                        }
                    ) {
                        Text("Save Attendance")
                    }
                }
            }
        }
    }
}


@Composable
private fun DateHeader(dateMillis: Long) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Date")
            Text(dateMillis.toReadableDate())
        }
    }
}

@Composable
private fun AttendanceSummary(
    present: Int,
    absent: Int
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            SummaryItem(
                label = "Present",
                value = present,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            SummaryItem(
                label = "Absent",
                value = absent,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
private fun SummaryItem(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}



@Composable
private fun AttendanceRowAdvanced(
    item: AttendanceUiItem,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(item.workerName)

            Row {
                FilterChip(
                    selected = item.status == AttendanceStatus.PRESENT,
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) },
                    label = { Text("Present") }
                )

                Spacer(Modifier.width(8.dp))

                FilterChip(
                    selected = item.status == AttendanceStatus.ABSENT,
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) },
                    label = { Text("Absent") }
                )
            }
        }
    }
}


