package com.rootlink.mystoremanager.ui.navigation

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
import com.rootlink.mystoremanager.ui.screen.report.*
import com.rootlink.mystoremanager.ui.screen.sales.*
import com.rootlink.mystoremanager.ui.screen.supplier.*
import com.rootlink.mystoremanager.ui.screen.worker.*
import com.rootlink.mystoremanager.ui.viewmodel.*

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

        // ================= WORKERS =================
        navigation(
            startDestination = Routes.WORKER_LIST,
            route = MainRoute.WORKERS.route
        ) {
            //Worker List
            composable(Routes.WORKER_LIST) {
                WorkerListScreen(
                    navController = navController
                )
            }

            //Add Worker
            composable(Routes.WORKER_ADD) {
                AddWorkerScreen(navController)
            }

            //Profile
            composable(
                route = Routes.WORKER_PROFILE,
                arguments = listOf(
                    navArgument("workerId") {
                        type = NavType.LongType
                    }
                )
            ) {
                WorkerProfileScreen(navController)
            }

            //Attendance
            composable(Routes.WORKER_ATTENDANCE) {
                AttendanceScreen(navController)
            }

            //Payment
            composable(
                Routes.WORKER_PAYMENT,
                arguments = listOf(navArgument("workerId") {
                    type = NavType.LongType
                })
            ) {
                WorkerPaymentScreen(
                    navController = navController
                )
            }

            //Ledger
            composable(
                Routes.WORKER_LEDGER,
                arguments = listOf(navArgument("workerId") {
                    type = NavType.LongType
                })
            ) {
                WorkerLedgerScreen(
                    navController = navController
                )
            }
        }

        // ================= SUPPLIERS =================
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

        // ================= SALES =================
        navigation(
            startDestination = Routes.SALES_HOME,
            route = MainRoute.SALES.route
        ) {

            composable(Routes.SALES_HOME) {
                SalesHomeScreen(
                    navController = navController
                )
            }

            composable(Routes.CREATE_SALE) {
                CreateSaleScreen(
                    navController = navController
                )
            }

            composable(
                Routes.INVOICE_VIEW,
                arguments = listOf(navArgument("saleId") {
                    type = NavType.LongType
                })
            ) {
                InvoiceViewScreen(
                    navController = navController
                )
            }
        }

        // ================= INVENTORY =================
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

        // ================= ACCOUNTING =================
        navigation(
            startDestination = Routes.TRANSACTION_LIST,
            route = MainRoute.ACCOUNTING.route
        ) {

            composable(Routes.TRANSACTION_LIST) {
                TransactionListScreen(
                    navController = navController
                )
            }

            composable(Routes.DAILY_SUMMARY) {
                DailySummaryScreen(navController)
            }
        }

        // ================= REPORTS =================
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
