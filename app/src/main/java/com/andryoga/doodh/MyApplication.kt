package com.andryoga.doodh

import android.app.Application
import com.andryoga.doodh.data.db.AppDatabase
import com.andryoga.doodh.data.db.DoodhDao

class MyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val doodhDao: DoodhDao by lazy { database.doodhDao() }
}