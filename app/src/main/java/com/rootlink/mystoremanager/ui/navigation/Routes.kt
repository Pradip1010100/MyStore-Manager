package com.rootlink.mystoremanager.ui.navigation

object Routes {

    // =========================
    // WORKER / WORK MANAGEMENT
    // =========================

    // List all workers (ENTRY POINT)
    const val WORKER_LIST = "worker_list"

    // Add new worker
    const val WORKER_ADD = "worker_add"

    // View / edit worker profile (optional but recommended)
    const val WORKER_PROFILE = "worker_profile/{workerId}"

    // Manual payment (Advance / Adjustment / Emergency)
    const val WORKER_PAYMENT = "worker_payment/{workerId}"

    // Auto-generate salary based on attendance
    //const val WORKER_SALARY_GENERATE = "worker_salary_generate/{workerId}"

    // Worker payment ledger
    const val WORKER_LEDGER = "worker_ledger/{workerId}"

    // Daily attendance marking (all workers)
    const val WORKER_ATTENDANCE = "worker_attendance"

    // Monthly attendance view for a worker
    const val WORKER_ATTENDANCE_DETAIL = "worker_attendance/{workerId}"

    // Worker salary & balance summary
    //const val WORKER_SUMMARY = "worker_summary/{workerId}"


    // =========================
    // SUPPLIERS & PURCHASE
    // =========================
    const val SUPPLIER_LIST = "supplier_list"
    const val PURCHASE_ENTRY = "purchase_entry/{supplierId}"
    const val SUPPLIER_LEDGER = "supplier_ledger/{supplierId}"


    // =========================
    // SALES & ORDERS
    // =========================
    const val SALES_HOME = "sales_home"
    const val CREATE_SALE = "create_sale"
    const val ORDER_LIST = "order_list"
    const val CREATE_ORDER = "create_order"
    const val INVOICE_VIEW = "invoice_view/{saleId}"


    // =========================
    // INVENTORY
    // =========================
    const val PRODUCT_LIST = "product_list"
    const val STOCK_OVERVIEW = "stock_overview"
    const val STOCK_ADJUSTMENT = "stock_adjustment/{productId}"


    // =========================
    // ACCOUNTING
    // =========================
    const val TRANSACTION_LIST = "transaction_list"
    const val DAILY_SUMMARY = "daily_summary"


    // =========================
    // REPORTS
    // =========================
    const val SALES_REPORT = "sales_report"
    const val PURCHASE_REPORT = "purchase_report"
    const val WORKER_REPORT = "worker_report"
    const val STOCK_REPORT = "stock_report"
    const val PROFIT_LOSS = "profit_loss"
}
