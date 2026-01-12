package com.rootlink.mystoremanager.ui.navigation

object Routes {

    // -------- Workers --------
    const val WORKER_LIST = "worker_list"
    const val WORKER_PAYMENT = "worker_payment/{workerId}"
    const val WORKER_LEDGER = "worker_ledger/{workerId}"

    // -------- Suppliers & Purchase --------
    const val SUPPLIER_LIST = "supplier_list"
    const val PURCHASE_ENTRY = "purchase_entry/{supplierId}"
    const val SUPPLIER_LEDGER = "supplier_ledger/{supplierId}"

    // -------- Sales & Orders --------
    const val SALES_HOME = "sales_home"
    const val CREATE_SALE = "create_sale"
    const val ORDER_LIST = "order_list"
    const val CREATE_ORDER = "create_order"
    const val INVOICE_VIEW = "invoice_view/{saleId}"

    // -------- Inventory --------
    const val PRODUCT_LIST = "product_list"
    const val STOCK_OVERVIEW = "stock_overview"
    const val STOCK_ADJUSTMENT = "stock_adjustment/{productId}"

    // -------- Accounting --------
    const val TRANSACTION_LIST = "transaction_list"
    const val DAILY_SUMMARY = "daily_summary"

    // -------- Reports --------
    const val SALES_REPORT = "sales_report"
    const val PURCHASE_REPORT = "purchase_report"
    const val WORKER_REPORT = "worker_report"
    const val STOCK_REPORT = "stock_report"
    const val PROFIT_LOSS = "profit_loss"
}
