package com.lucamesquitaa.controledegastoskotlin

import android.app.Application
import com.lucamesquitaa.controledegastoskotlin.model.AppDatabase

class App: Application() {
    lateinit var db: AppDatabase
    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(this)
    }
}