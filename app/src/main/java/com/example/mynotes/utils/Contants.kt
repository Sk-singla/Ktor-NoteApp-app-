package com.example.mynotes.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*

object Contants {

    const val API_VERSION = "v1"
    const val BASE_URL = "https://myfirstnotes.herokuapp.com"
    const val USER_TOKEN = "user_token"

    // ROOM DATABASE MIGRATION
    val MIGRATION_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE note_local ADD deleteLocal INTEGER NOT NULL DEFAULT 0")
        }
    }

    const val COOKIE_KEY = "PREF_COOKIES_KEY"
    const val COOKIE_SHARED_PREF = "COOKIE_SHARED_PREF"
}