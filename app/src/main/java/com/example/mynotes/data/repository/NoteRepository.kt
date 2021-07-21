package com.example.mynotes.data.repository


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.mynotes.data.local.dao.NoteDao
import com.example.mynotes.data.local.model.NoteLocal
import com.example.mynotes.data.remote.NoteApi
import com.example.mynotes.data.remote.model.RemoteNote
import com.example.mynotes.data.remote.model.User
import com.example.mynotes.utils.Result
import com.example.mynotes.utils.SessionManager
import com.example.mynotes.utils.toNoteLocal
import com.example.mynotes.utils.toRemoteNote
import java.lang.Exception
import javax.inject.Inject

class NoteRepository @Inject constructor(
    val noteDao: NoteDao,
    val noteApi:NoteApi,
    val sessionManager: SessionManager,
    val context: Context
){
    val token:String? = sessionManager.fetchAuthToken()

    /**
     * Insert single note
     */
    suspend fun insertNote(note:NoteLocal){
        noteDao.insertNote(note)

        val remoteResponse = try {
            if(isNetworkConnected()) {
                noteApi.insertNote(
                        "Bearer ${token!!}",
                        RemoteNote(note.noteTitle, note.description, note.date, note.noteId)
                )
            } else {
                null
            }
        } catch (e:Exception){
            Log.d("InsertNote","Exception Occurred while uploading note: ${e.message}")
            null
        }

        if(remoteResponse!=null && remoteResponse.isSuccessful){
            noteDao.insertNote(note.apply { connected = true })
        }
    }


    /**
     * Update Note
     */
    suspend fun updateNote(note:NoteLocal){
        noteDao.insertNote(note)

        val remoteResponse = try {
            if(isNetworkConnected()){
                noteApi.updateRemoteNote(
                        "Bearer ${token!!}",
                        RemoteNote(note.noteTitle,note.description,note.date,note.noteId)
                )
            } else {
                null
            }
        } catch (e:Exception){
            Log.d("UpdateNote","Exception: ${e.message}")
            null
        }
        if(remoteResponse!=null && remoteResponse.isSuccessful){
            noteDao.insertNote(note.apply { connected = true })
        }
    }

    /**
     * Refresh All notes
     */
    suspend fun refreshNotes() {
        val response = try {
            if(isNetworkConnected()){
                noteApi.getRemoteNotes(
                        "Bearer ${token!!}"
                )
            } else {
                null
            }
        } catch (e:Exception){
            null
        }

        if(response!=null && response.isSuccessful && response.body()!=null){
            val body = response.body()

            // INSERT LOCALLY
            body?.let { remoteNotes->
                noteDao.insertNote(*remoteNotes.toNoteLocal(connected = true, deleteLocal = false))
            }
        }
    }

    /**
     * Get all offline notes
     */
    fun getLocalNotes() = noteDao.getAllNotesOrderedByDate()


    /**
     * Update List Of Notes
     */
    suspend fun updateNotesList(notes:List<NoteLocal>){
        notes.forEach {
            updateNote(it)
        }
    }



    /**
     * Sync Notes
     */
    suspend fun syncNotes():Boolean{
        try {
            val deletedNotes = noteDao.getLocallyDeletedNotes()
            Log.d("DELETEDNOTES","${deletedNotes}")
            if(deletedNotes.isNotEmpty() && isNetworkConnected()){
                val response = noteApi.deleteRemoteNotesList(
                        "Bearer ${token!!}",
                        deletedNotes.map { it.noteId }
                )
                if(response!=null && response.isSuccessful){
                    for(note in deletedNotes){
                        noteDao.deleteNote(note)
                    }
                }
            }


            val notSyncNotes = noteDao.getAllNotSynchedNotes()
            Log.d("NotSynchedNotes","${notSyncNotes}")
            if(notSyncNotes.isNotEmpty() && isNetworkConnected()){
                val token = sessionManager.fetchAuthToken()
                val response = noteApi.insertNotesList(
                        "Bearer ${token!!}",
                        notSyncNotes.toRemoteNote()
                )

                if(!response.isSuccessful){
                    updateNotesList(notSyncNotes)
                }
                return true

            } else {
                return false
            }

        } catch (e:Exception){
            Log.d("SYNCNOTES","Exception While Sync: ${e.message}")
            return false
        }
    }


    /**
     * Network connected or not
     */
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    /**
     * Delete Note
     */
    suspend fun deleteNote(note:NoteLocal){

        if(!note.connected){
            noteDao.deleteNote(note)
            return
        }

        val response = try {
            if(isNetworkConnected()){
                noteApi.deleteRemoteNote(
                        "Bearer ${token!!}",
                        note.noteId
                )
            } else {
                null
            }
        } catch (e:Exception){
            Log.d("DELETENOTE","Delete Note Exception: ${e.message}")
            null
        }

        if(response!=null && response.isSuccessful){
            noteDao.deleteNote(note)
        } else{
            noteDao.updateDeleteLocal(note.noteId)
        }
    }

    /**
     *  Register User
     */
    suspend fun createUser(user:User): Result<String> {
        if(!isNetworkConnected()){
            return Result.Error("Internet Not Connected")
        }

        return try {
            val response = noteApi.createUser(user)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if(responseBody?.success!!) {
                    Result.Success(response.body()?.message!!)
                }else{
                    Result.Error("An error Occurred: ${response.body()?.message!!}")
                }
            } else {
                Result.Error("Not Successful")
            }
        } catch (e:Exception){
            Log.d("USERERROR", "${e.message}")
            Result.Error("An error occurred: ${e.message}")
        }
    }

    /**
     * Find Current User or check if user logged in or not
     */
    suspend fun findCurUser(): Result<User>{
        if(!isNetworkConnected()){
            return Result.Error("Internet Not Connected")
        }

        return try {
            val response = noteApi.getCurrentUser()
            if(response.isSuccessful){
                Log.d("USER","${response.body()}")
                Result.Success(response.body()!!)
            } else {
                Result.Error("Not Successful",response.body())
            }
        } catch (e: Exception){
            Result.Error("Error Occurred: ${e.message}")
        }
    }

    /**
     * Logout User
     */
    suspend fun logout(){
        if(!isNetworkConnected()){
            return
        }

        val response = noteApi.logout()
        if(response.isSuccessful){
            sessionManager.clearAuthToken()
            sessionManager.clearCookie()
            Log.d("TOKEN", "Success: ${sessionManager.fetchAuthToken()}")
        }
        else {
            Log.d("TOKEN", "Failure: ${sessionManager.fetchAuthToken()}")
        }
        Log.d("TOKEN", "${sessionManager.fetchAuthToken()}")
    }

    /**
     * Login User
     */
    suspend fun loginUser(user:User): Result<String> {
        if(!isNetworkConnected()){
            return Result.Error("Internet Not Connected")
        }

        return try {
            val response = noteApi.loginUser(user)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if(responseBody?.success!!) {
                    Result.Success(responseBody.message)
                }else{
                    Result.Error("User Not ")
                }
            } else {
                Result.Error("Error while Login User")
            }
        } catch (e:Exception){
            Log.d("USERERROR", "${e.message}")
            Result.Error("Unknown error occurred, ${e.message}")
        }
    }


}