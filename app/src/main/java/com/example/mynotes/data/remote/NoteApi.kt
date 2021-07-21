package com.example.mynotes.data.remote


import com.example.mynotes.data.remote.model.RemoteNote
import com.example.mynotes.data.remote.model.RemoteResponse
import com.example.mynotes.data.remote.model.User
import com.example.mynotes.utils.Contants.API_VERSION
import retrofit2.Response
import retrofit2.http.*

interface NoteApi {

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/create")
    suspend fun createUser(
        @Body user: User
    ): Response<RemoteResponse>

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/login")
    suspend fun loginUser(
            @Body user: User
    ): Response<RemoteResponse>


    @GET("$API_VERSION/users/current")
    suspend fun getCurrentUser(): Response<User?>

    @GET("$API_VERSION/users/logout")
    suspend fun logout(): Response<RemoteResponse>


    // ============= NOTES =============

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes")
    suspend fun insertNote(
        @Header("Authorization") token:String,
        @Body note:RemoteNote
    ): Response<RemoteResponse>

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/list")
    suspend fun insertNotesList(
            @Header("Authorization") token:String,
            @Body notes:List<RemoteNote>
    ): Response<RemoteResponse>

    @GET("$API_VERSION/notes")
    suspend fun getRemoteNotes(
            @Header("Authorization") token: String
    ):Response<List<RemoteNote>>

    @POST("$API_VERSION/notes/delete")
    suspend fun deleteRemoteNote(
            @Header("Authorization") token: String,
            @Body noteId:String
    ):Response<RemoteResponse>

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/delete/list")
    suspend fun deleteRemoteNotesList(
            @Header("Authorization") token: String,
            @Body listOfNoteId:List<String>
    ):Response<RemoteResponse>

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/update")
    suspend fun updateRemoteNote(
            @Header("Authorization") token: String,
            @Body note:RemoteNote
    ):Response<RemoteResponse>

}