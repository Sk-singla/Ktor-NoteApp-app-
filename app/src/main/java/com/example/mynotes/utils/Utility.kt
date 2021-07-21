package com.example.mynotes.utils

import com.example.mynotes.data.local.model.NoteLocal
import com.example.mynotes.data.remote.model.RemoteNote
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun millToDate(date:Long):String{
    val date = Date(date)
    val simpleDateFormat = SimpleDateFormat("hh:mm a | MMM d, yyyy")

    return simpleDateFormat.format(date)
}

fun checkEmailValid(email:String):Boolean{
    var regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    var pat = Pattern.compile(regex)
    return (email.isNotEmpty() && pat.matcher(email).matches())
}

fun List<NoteLocal>.toRemoteNote() = map { noteLocal ->
    RemoteNote(
            noteTitle = noteLocal.noteTitle,
            description = noteLocal.description,
            date = noteLocal.date,
            id = noteLocal.noteId
    )
}


fun List<RemoteNote>.toNoteLocal(connected:Boolean, deleteLocal:Boolean) = map { remoteNote ->
    NoteLocal(
            remoteNote.noteTitle,
            remoteNote.description,
            remoteNote.date,
            connected,
            deleteLocal,
            remoteNote.id
    )
}.toTypedArray()
