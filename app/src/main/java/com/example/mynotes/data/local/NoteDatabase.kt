package com.example.mynotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynotes.data.local.dao.NoteDao
import com.example.mynotes.data.local.model.NoteLocal

@Database(entities = [NoteLocal::class], version = 2,exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
}