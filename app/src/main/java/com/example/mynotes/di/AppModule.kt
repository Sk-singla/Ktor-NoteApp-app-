package com.example.mynotes.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.mynotes.data.local.NoteDatabase
import com.example.mynotes.data.local.dao.NoteDao
import com.example.mynotes.data.remote.NoteApi
import com.example.mynotes.data.remote.RetrofitInstance
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.utils.Contants.MIGRATION_1_2
import com.example.mynotes.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLocalNoteDatabase(
        @ApplicationContext context:Context
    ) = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "note_db"
    ).addMigrations(MIGRATION_1_2).build()


    @Singleton
    @Provides
    fun provideNoteDao(
        db: NoteDatabase
    ) = db.noteDao()

    @Singleton
    @Provides
    fun provideNoteApi(
            @ApplicationContext context: Context
    ) = RetrofitInstance(context).api

    @Singleton
    @Provides
    fun provideNoteRepository(
            noteDao:NoteDao,
            noteApi: NoteApi,
            sessionManager: SessionManager,
            @ApplicationContext context: Context
    ) = NoteRepository(noteDao,noteApi,sessionManager,context)

    @Singleton
    @Provides
    fun provideSessionManager(
            @ApplicationContext context: Context
    ) = SessionManager(context)
}