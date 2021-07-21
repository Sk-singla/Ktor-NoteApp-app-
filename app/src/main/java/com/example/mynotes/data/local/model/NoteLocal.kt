package com.example.mynotes.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "note_local")
data class NoteLocal(
        var noteTitle:String?=null,
        var description:String?=null,
        var date:Long=0L,
        var connected:Boolean = false,
        var deleteLocal:Boolean = false,
        @PrimaryKey(autoGenerate = false)
        var noteId:String = UUID.randomUUID().toString()
):Serializable
