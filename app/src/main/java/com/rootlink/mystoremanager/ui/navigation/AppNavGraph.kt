package com.rootlink.mystoremanager.ui.navigation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.rootlink.mystoremanager.ui.screen.accounting.DailySummaryScreen
import com.rootlink.mystoremanager.ui.screen.accounting.TransactionListScreen
import com.rootlink.mystoremanager.ui.screen.inventory.ProductListScreen
import com.rootlink.mystoremanager.ui.screen.inventory.StockAdjustmentScreen
import com.rootlink.mystoremanager.ui.screen.inventory.StockOverviewScreen
import com.rootlink.mystoremanager.ui.screen.order.CreateOrderScreen
import com.rootlink.mystoremanager.ui.screen.order.OrderListScreen
import com.rootlink.mystoremanager.ui.screen.report.ProfitLossScreen
import com.rootlink.mystoremanager.ui.screen.report.PurchaseReportScreen
import com.rootlink.mystoremanager.ui.screen.report.SalesReportScreen
import com.rootlink.mystoremanager.ui.screen.report.StockReportScreen
import com.rootlink.mystoremanager.ui.screen.report.WorkerReportScreen
import com.rootlink.mystoremanager.ui.screen.sales.CreateSaleScreen
import com.rootlink.mystoremanager.ui.screen.sales.InvoiceViewScreen
import com.rootlink.mystoremanager.ui.screen.sales.SalesHomeScreen
import com.rootlink.mystoremanager.ui.screen.supplier.PurchaseEntryScreen
import com.rootlink.mystoremanager.ui.screen.supplier.SupplierLedgerScreen
import com.rootlink.mystoremanager.ui.screen.supplier.SupplierListScreen
import com.rootlink.mystoremanager.ui.screen.worker.WorkerLedgerScreen
import com.rootlink.mystoremanager.ui.screen.worker.WorkerListScreen
import com.rootlink.mystoremanager.ui.screen.worker.WorkerPaymentScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.WORKERS.route,
        modifier = modifier
    ) {

        // ---------------- Workers ----------------
        navigation(
            startDestination = Routes.WORKER_LIST,
            route = MainRoute.WORKERS.route
        ) {

            composable(Routes.WORKER_LIST) {
                WorkerListScreen(navController)
            }

            composable(
                Routes.WORKER_PAYMENT,
                arguments = listOf(navArgument("workerId") {
                    type = NavType.LongType
                })
            ) {
                WorkerPaymentScreen(navController)
            }

            composable(
                Routes.WORKER_LEDGER,
                arguments = listOf(navArgument("workerId") {
                    type = NavType.LongType
                })
            ) {
                WorkerLedgerScreen(navController)
            }
        }

        // ---------------- Suppliers & Purchase ----------------
        navigation(
            startDestination = Routes.SUPPLIER_LIST,
            route = MainRoute.SUPPLIERS.route
        ) {

            composable(Routes.SUPPLIER_LIST) {
                SupplierListScreen(navController)
            }

            composable(
                Routes.PURCHASE_ENTRY,
                arguments = listOf(navArgument("supplierId") {
                    type = NavType.LongType
                })
            ) {
                PurchaseEntryScreen(navController)
            }

            composable(
                Routes.SUPPLIER_LEDGER,
                arguments = listOf(navArgument("supplierId") {
                    type = NavType.LongType
                })
            ) {
                SupplierLedgerScreen(navController)
            }
        }

        // ---------------- Sales & Orders ----------------
        navigation(
            startDestination = Routes.SALES_HOME,
            route = MainRoute.SALES.route
        ) {

            composable(Routes.SALES_HOME) {
                SalesHomeScreen(navController)
            }

            composable(Routes.CREATE_SALE) {
                CreateSaleScreen(navController)
            }

            composable(Routes.ORDER_LIST) {
                OrderListScreen(navController)
            }

            composable(Routes.CREATE_ORDER) {
                CreateOrderScreen(navController)
            }

            composable(
                Routes.INVOICE_VIEW,
                arguments = listOf(navArgument("saleId") {
                    type = NavType.LongType
                })
            ) {
                InvoiceViewScreen(navController)
            }
        }

        // ---------------- Inventory ----------------
        navigation(
            startDestination = Routes.PRODUCT_LIST,
            route = MainRoute.INVENTORY.route
        ) {

            composable(Routes.PRODUCT_LIST) {
                ProductListScreen(navController)
            }

            composable(Routes.STOCK_OVERVIEW) {
                StockOverviewScreen(navController)
            }

            composable(
                Routes.STOCK_ADJUSTMENT,
                arguments = listOf(navArgument("productId") {
                    type = NavType.LongType
                })
            ) {
                StockAdjustmentScreen(navController)
            }
        }

        // ---------------- Accounting ----------------
        navigation(
            startDestination = Routes.TRANSACTION_LIST,
            route = MainRoute.ACCOUNTING.route
        ) {

            composable(Routes.TRANSACTION_LIST) {
                TransactionListScreen(navController)
            }

            composable(Routes.DAILY_SUMMARY) {
                DailySummaryScreen(navController)
            }
        }

        // ---------------- Reports ----------------
        navigation(
            startDestination = Routes.SALES_REPORT,
            route = MainRoute.REPORTS.route
        ) {

            composable(Routes.SALES_REPORT) {
                SalesReportScreen(navController)
            }

            composable(Routes.PURCHASE_REPORT) {
                PurchaseReportScreen(navController)
            }

            composable(Routes.WORKER_REPORT) {
                WorkerReportScreen(navController)
            }

            composable(Routes.STOCK_REPORT) {
                StockReportScreen(navController)
            }

            composable(Routes.PROFIT_LOSS) {
                ProfitLossScreen(navController)
            }
        }
    }
}
