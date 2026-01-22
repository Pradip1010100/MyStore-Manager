package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import com.rootlink.mystoremanager.data.entity.CustomerEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime

/* -------------------------------------------------------------------------- */
/*                              COLORS (PRINT SAFE)                            */
/* -------------------------------------------------------------------------- */

private val TableHeaderColor = Color(0xFFE0E0E0)
private val BorderColor = Color(0xFFBDBDBD)

/* -------------------------------------------------------------------------- */
/*                               MAIN SCREEN                                   */
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

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {

            uiState.companyProfile?.let {
                InvoiceHeader(it)
            }
            Spacer(Modifier.height(16.dp))
            InvoiceMeta(sale)
            Spacer(Modifier.height(16.dp))
            BillToSection(uiState.selectedCustomer)
            Spacer(Modifier.height(16.dp))

            InvoiceItemTable(
                items = uiState.invoiceItems,
                productNames = uiState.productNameMap
            )

            Spacer(Modifier.height(16.dp))

            InvoiceTotals(
                sale = sale,
                oldBatteryAmount = uiState.oldBatteryAmount
            )

            Spacer(Modifier.height(24.dp))
            InvoiceFooter()
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                   HEADER                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceHeader(company: CompanyProfileEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(company.name, fontWeight = FontWeight.Bold)
            Text(company.businessType)
            Text(company.address)
            Text("Phone: ${company.phone}")
        }

        Text(
            "INVOICE",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}


/* -------------------------------------------------------------------------- */
/*                                   META                                     */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceMeta(sale: SaleEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Invoice No: INV-${sale.saleId}")
            Text("Date: ${sale.saleDate.toReadableDateTime()}")
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                 BILL TO                                    */
/* -------------------------------------------------------------------------- */

@Composable
private fun BillToSection(customer: CustomerEntity?) {
    Column {
        Text("Bill To", fontWeight = FontWeight.Bold)
        Text(customer?.name ?: "Walk-in Customer")
        Text(customer?.phone ?: "-")
        Text(customer?.address ?: "-")
    }
}

/* -------------------------------------------------------------------------- */
/*                               ITEM TABLE                                   */
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
                .padding(vertical = 6.dp)
        ) {
            Cell("S.No", 0.7f, FontWeight.Bold, TextAlign.Center)
            Cell("Description", 3f, FontWeight.Bold)
            Cell("Qty", 1f, FontWeight.Bold, TextAlign.Center)
            Cell("Unit Price", 1.5f, FontWeight.Bold, TextAlign.End)
            Cell("Amount", 1.5f, FontWeight.Bold, TextAlign.End)
        }

        Divider(color = BorderColor)

        items.forEachIndexed { index, item ->

            val unitPrice =
                if (item.quantity == 0) 0.0 else item.lineTotal / item.quantity

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Cell((index + 1).toString(), 0.7f, align = TextAlign.Center)
                Cell(productNames[item.productId] ?: "Item", 3f)
                Cell(item.quantity.toString(), 1f, align = TextAlign.Center)
                Cell("₹%.2f".format(unitPrice), 1.5f, align = TextAlign.End)
                Cell("₹%.2f".format(item.lineTotal), 1.5f, align = TextAlign.End)
            }

            Divider(color = BorderColor)
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                  CELL                                      */
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
        textAlign = align
    )
}

/* -------------------------------------------------------------------------- */
/*                                  TOTALS                                    */
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
            TotalRow("Old Battery Exchange", -oldBatteryAmount)
        }

        Divider(
            modifier = Modifier
                .width(260.dp)
                .padding(vertical = 6.dp)
        )

        TotalRow(
            "Total Amount",
            sale.finalAmount,
            bold = true
        )
    }
}

@Composable
private fun TotalRow(
    label: String,
    amount: Double,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.width(260.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            "₹%.2f".format(amount),
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/* -------------------------------------------------------------------------- */
/*                                  FOOTER                                    */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceFooter() {
    Text(
        "Thank you for your business",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
