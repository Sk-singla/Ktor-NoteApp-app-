package com.example.mynotes.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mynotes.data.local.model.NoteLocal

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(vararg note:NoteLocal)

    @Query("SELECT * FROM note_local WHERE deleteLocal=0 ORDER BY date DESC")
    fun getAllNotesOrderedByDate(): LiveData<List<NoteLocal>>

    @Delete
    suspend fun deleteNote(note: NoteLocal)

    @Query("UPDATE note_local SET deleteLocal= 1 WHERE noteId = :mNoteId")
    suspend fun updateDeleteLocal(mNoteId:String)

    @Query("SELECT * FROM note_local WHERE connected=0")
    suspend fun getAllNotSynchedNotes(): List<NoteLocal>

    @Query("SELECT * FROM note_local WHERE deleteLocal=1")
    suspend fun getLocallyDeletedNotes():List<NoteLocal>

}