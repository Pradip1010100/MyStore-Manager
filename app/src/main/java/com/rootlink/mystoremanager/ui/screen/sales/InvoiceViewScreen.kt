package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.*
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime

/* -------------------------------------------------------------------------- */
/* COLORS */
/* -------------------------------------------------------------------------- */

private val TableHeaderColor = Color(0xFFF1F1F1)
private val BorderColor = Color(0xFFDDDDDD)

/* -------------------------------------------------------------------------- */
/* MAIN */
/* -------------------------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceViewScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()
    val context = LocalContext.current

    val saleId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("saleId") ?: return

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(saleId) {
        viewModel.loadInvoice(saleId)
    }

    val sale = uiState.selectedSale ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.shareInvoicePdf(
                                context,
                                sale,
                                uiState.invoiceItems
                            )
                        }
                    ) {
                        Icon(Icons.Default.Share, null)
                    }
                }
            )
        }
    ) { padding ->

        /* 🔑 ONLY CHANGE: verticalScroll added */
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            uiState.companyProfile?.let {
                InvoiceHeader(it)
            }

            InvoiceMeta(sale)

            BillToSection(uiState.selectedCustomer)

            InvoiceItemTable(
                items = uiState.invoiceItems,
                productNames = uiState.productNameMap
            )

            InvoiceTotals(
                sale = sale,
                oldBatteryAmount = uiState.oldBatteryAmount
            )

            InvoiceFooter()
        }
    }
}

/* -------------------------------------------------------------------------- */
/* HEADER */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceHeader(company: CompanyProfileEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(company.name, fontWeight = FontWeight.Bold)
            Text(company.businessType, style = MaterialTheme.typography.bodySmall)
            Text(company.address, style = MaterialTheme.typography.bodySmall)
            Text("Phone: ${company.phone}", style = MaterialTheme.typography.bodySmall)
        }

        Text("INVOICE", fontWeight = FontWeight.Bold)
    }
}

/* -------------------------------------------------------------------------- */
/* META */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceMeta(sale: SaleEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Invoice No: INV-${sale.saleId}", style = MaterialTheme.typography.bodySmall)
        Text(
            "Date: ${sale.saleDate.toReadableDateTime()}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/* -------------------------------------------------------------------------- */
/* BILL TO */
/* -------------------------------------------------------------------------- */

@Composable
private fun BillToSection(customer: CustomerEntity?) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("Bill To", fontWeight = FontWeight.Bold)
        Text(customer?.name ?: "Walk-in Customer")
        Text(customer?.phone ?: "-", style = MaterialTheme.typography.bodySmall)
        Text(customer?.address ?: "-", style = MaterialTheme.typography.bodySmall)
    }
}

/* -------------------------------------------------------------------------- */
/* ITEM TABLE (UNCHANGED STRUCTURE) */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceItemTable(
    items: List<SaleItemEntity>,
    productNames: Map<Long, String>
) {
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderColor)
                .padding(vertical = 4.dp)
        ) {
            Cell("No", 0.6f, FontWeight.Bold, TextAlign.Center)
            Cell("Item", 3f, FontWeight.Bold)
            Cell("Qty", 0.8f, FontWeight.Bold, TextAlign.Center)
            Cell("Amt", 1.2f, FontWeight.Bold, TextAlign.End)
        }

        Divider(color = BorderColor)

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Cell((index + 1).toString(), 0.6f, align = TextAlign.Center)
                Cell(productNames[item.productId] ?: "Item", 3f)
                Cell(item.quantity.toString(), 0.8f, align = TextAlign.Center)
                Cell(
                    "₹%.2f".format(item.lineTotal),
                    1.2f,
                    align = TextAlign.End
                )
            }
            Divider(color = BorderColor)
        }
    }
}

/* -------------------------------------------------------------------------- */
/* CELL */
/* -------------------------------------------------------------------------- */

@Composable
private fun RowScope.Cell(
    text: String,
    weight: Float,
    weightFont: FontWeight = FontWeight.Normal,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontWeight = weightFont,
        textAlign = align,
        style = MaterialTheme.typography.bodySmall
    )
}

/* -------------------------------------------------------------------------- */
/* TOTALS */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceTotals(
    sale: SaleEntity,
    oldBatteryAmount: Double?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {

        TotalRow("Subtotal", sale.totalAmount)

        if (sale.discount > 0) {
            TotalRow("Discount", -sale.discount)
        }

        if (oldBatteryAmount != null && oldBatteryAmount > 0) {
            TotalRow("Old Battery", -oldBatteryAmount)
        }

        Divider(
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 6.dp)
        )

        TotalRow("Total", sale.finalAmount, bold = true)
    }
}

@Composable
private fun TotalRow(
    label: String,
    amount: Double,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.width(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(
            "₹%.2f".format(amount),
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/* -------------------------------------------------------------------------- */
/* FOOTER */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceFooter() {
    Text(
        "Thank you for your business",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
    )
}
