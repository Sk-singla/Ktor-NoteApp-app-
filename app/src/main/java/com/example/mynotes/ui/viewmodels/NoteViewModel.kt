package com.example.mynotes.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.local.model.NoteLocal
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    val repo: NoteRepository
):ViewModel() {

    val notes: LiveData<List<NoteLocal>> = repo.getLocalNotes()


    init {
        viewModelScope.launch {
            repo.refreshNotes()
        }
    }

    fun addNote(note:NoteLocal) = viewModelScope.launch{
        repo.insertNote(note)
    }


    fun syncNotes(action:(()->Unit)?=null) = viewModelScope.launch{
        repo.syncNotes()
        action?.invoke()
    }

    fun deleteNote(note:NoteLocal) = viewModelScope.launch(Dispatchers.IO){
        repo.deleteNote(note)
    }

    fun updateNote(note:NoteLocal) = viewModelScope.launch(Dispatchers.IO) {
        repo.updateNote(note)
    }
}