package com.rootlink.mystoremanager.data.di

import com.rootlink.mystoremanager.data.database.AppDatabase

object DatabaseCloser {
    @Volatile
    var db: AppDatabase? = null
}
