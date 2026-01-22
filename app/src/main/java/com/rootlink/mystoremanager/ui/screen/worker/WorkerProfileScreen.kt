package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.enums.AttendanceStatus
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel
import com.rootlink.mystoremanager.ui.viewmodel.state.WorkerUiState
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProfileScreen(
    navController: NavController,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val workerId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("workerId")
            ?: return

    LaunchedEffect(workerId) {
        viewModel.loadWorker(workerId)
    }

    val worker = uiState.selectedWorker

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Worker Profile") })
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

            worker == null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Worker not found")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    /* ================= PROFILE HEADER ================= */
                    ProfileHeader(worker)

                    /* ================= QUICK ACTIONS ================= */
                    ActionGrid(
                        worker = worker,
                        navController = navController
                    )

                    /* ================= ATTENDANCE CALENDAR ================= */
                    AttendanceCalendar(
                        workerId = worker.workerId,
                        uiState = uiState,
                        viewModel = viewModel
                    )


                    /* ================= INFO SECTIONS ================= */
                    InfoCard(title = "Personal Information") {
                        InfoRow("Phone", worker.phone ?: "-")
                        InfoRow("Team", worker.team ?: "-")
                        InfoRow("Position", worker.position ?: "-")
                    }

                    InfoCard(title = "Work Information") {
                        InfoRow("Status", worker.status.name)
                    }

                    InfoCard(title = "Salary Information") {
                        InfoRow("Salary Type", worker.salaryType.name)
                        InfoRow(
                            "Rate",
                            when (worker.salaryType) {
                                SalaryType.DAILY -> "₹${worker.salaryAmount} / day"
                                SalaryType.MONTHLY -> "₹${worker.salaryAmount} / month"
                                SalaryType.PER_JOB -> "₹${worker.salaryAmount} / job"
                            }
                        )
                    }

                    /* ================= DEACTIVATE ================= */
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                if (worker.status.name == "ACTIVE")
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            if (worker.status.name == "ACTIVE") {
                                viewModel.deactivateWorker(worker.workerId)
                            } else {
                                viewModel.activateWorker(worker.workerId)
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Text(
                            if (worker.status.name == "ACTIVE")
                                "Deactivate Worker"
                            else
                                "Activate Worker"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(worker: WorkerEntity) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = worker.name,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = worker.status.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActionGrid(
    worker: WorkerEntity,
    navController: NavController
) {
    val isActive = worker.status.name == "ACTIVE"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                /* ---------- PAY (DISABLED WHEN INACTIVE) ---------- */
                ActionIcon(
                    icon = Icons.Default.Payments,
                    label = "Pay",
                    enabled = isActive
                ) {
                    navController.navigate(
                        "worker_payment/${worker.workerId}"
                    )
                }

                /* ---------- LEDGER (ALWAYS ENABLED) ---------- */
                ActionIcon(
                    icon = Icons.Default.ReceiptLong,
                    label = "Ledger"
                ) {
                    navController.navigate(
                        "worker_ledger/${worker.workerId}"
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionIcon(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled) { onClick() }
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.size(56.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (enabled)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint =
                        if (enabled)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color =
                if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.outline
        )
    }
}


@Composable
fun AttendanceCalendar(
    workerId: Long,
    uiState: WorkerUiState,
    viewModel: WorkerViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(workerId, currentMonth) {
        viewModel.loadWorkerAttendanceForMonth(
            workerId,
            currentMonth
        )
    }

    val attendanceMap = uiState.monthlyAttendance

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Attendance",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "<",
                    modifier = Modifier
                        .clickable { currentMonth = currentMonth.minusMonths(1) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = currentMonth.month.name.lowercase()
                        .replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = ">",
                    modifier = Modifier
                        .clickable { currentMonth = currentMonth.plusMonths(1) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }


            Spacer(Modifier.height(12.dp))

            CalendarGrid(
                month = currentMonth,
                attendanceMap = attendanceMap
            )

            Spacer(Modifier.height(12.dp))

            AttendanceLegend()
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    attendanceMap: Map<Long, AttendanceStatus>
) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfWeek =
        month.atDay(1).dayOfWeek.value % 7 // Sunday = 0

    Column {
        DayHeaders()

        var day = 1 - firstDayOfWeek

        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )
            {
                repeat(7) {
                    if (day in 1..daysInMonth) {
                        val dateMillis =
                            month.atDay(day)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()

                        val status =
                            attendanceMap[dateMillis]
                                ?: AttendanceStatus.UNMARKED

                        val todayMillis =
                            LocalDate.now()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()

                        val isToday = dateMillis == todayMillis
                        val isFuture = dateMillis > todayMillis

                        CalendarDay(
                            day = day,
                            status = status,
                            isToday = isToday,
                            isFuture = isFuture
                        )

                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                    day++
                }
            }
        }
    }
}
@Composable
private fun DayHeaders() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    status: AttendanceStatus,
    isToday: Boolean,
    isFuture: Boolean
) {
    val backgroundColor: Color
    val textColor: Color
    val borderColor: Color?

    when {
        isFuture -> {
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            textColor = MaterialTheme.colorScheme.outline
            borderColor = null
        }

        status == AttendanceStatus.PRESENT -> {
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
            borderColor = MaterialTheme.colorScheme.primary // ✅ GREEN BORDER
        }

        status == AttendanceStatus.ABSENT -> {
            backgroundColor = MaterialTheme.colorScheme.errorContainer
            textColor = MaterialTheme.colorScheme.onErrorContainer
            borderColor = MaterialTheme.colorScheme.error // ✅ RED BORDER
        }

        else -> {
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            textColor = MaterialTheme.colorScheme.onSurfaceVariant
            borderColor = null
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .then(
                if (borderColor != null)
                    Modifier.border(
                        width = 2.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                else Modifier
            )
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
        )
    }
}



@Composable
private fun AttendanceLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem("Present", MaterialTheme.colorScheme.primaryContainer)
        LegendItem("Absent", MaterialTheme.colorScheme.errorContainer)
        LegendItem("Unmarked", MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}



@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}
