package com.rootlink.mystoremanager.ui.navigation

object Routes {

    // =========================
    // WORKER / WORK MANAGEMENT
    // =========================

    const val WORKER_LIST = "worker_list"
    const val WORKER_ADD = "worker_add"
    const val WORKER_PROFILE = "worker_profile/{workerId}"
    const val WORKER_PAYMENT = "worker_payment/{workerId}"
    const val WORKER_LEDGER = "worker_ledger/{workerId}"
    const val WORKER_ATTENDANCE = "worker_attendance"
    const val WORKER_ATTENDANCE_DETAIL = "worker_attendance/{workerId}"

    // =========================
    // SUPPLIERS & PURCHASE
    // =========================

    const val SUPPLIER_LIST = "supplier_list"
    const val SUPPLIER_ADD = "supplier_add"
    const val SUPPLIER_DETAIL = "supplier_detail/{supplierId}"
    const val SUPPLIER_LEDGER = "supplier_ledger/{supplierId}"
    const val SUPPLIER_PAYMENT = "supplier_payment/{supplierId}"
    const val PURCHASE_ENTRY = "purchase_entry/{supplierId}"
    const val SUPPLIER_PROFILE = "supplier_profile/{supplierId}"

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
    const val PRODUCT_ADD = "product_add"
    const val STOCK_OVERVIEW = "stock_overview"
    const val STOCK_ADJUSTMENT = "stock_adjustment/{productId}"
    const val PRODUCT_INVENTORY = "product_inventory/{productId}"

    // ✅ NEW
    const val STOCK_HISTORY = "stock_history/{productId}"
    const val LOW_STOCK = "low_stock"

    // =========================
    // ACCOUNTING
    // =========================

    const val TRANSACTION_LIST = "transaction_list"
    const val DAILY_SUMMARY = "daily_summary"

    // =========================
    // DASHBOARD
    // =========================

    const val DASHBOARD = "dashboard_home"
    const val PERSONAL_TRANSACTION = "personal_transaction"

    const val SALES_REPORT = "sales_report"
    const val PURCHASE_REPORT = "purchase_report"
    const val WORKER_REPORT = "worker_report"
    const val STOCK_REPORT = "stock_report"
    const val PROFIT_LOSS = "profit_loss"
}
