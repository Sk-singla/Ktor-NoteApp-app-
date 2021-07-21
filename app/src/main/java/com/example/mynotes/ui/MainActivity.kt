package com.example.mynotes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.mynotes.R
import com.example.mynotes.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        deleteDatabase("note_db")

    }
}


/**
 * TODO: 1. In Intellij Idea( Api) make route to upload list of notes to server - Done
 *
 * Todo: 2. Make function to upload list of notes in app - Done
 *
 * Todo: 3. add Sync property in Notes class -  MAINTAIN
 *
 * Todo: 4. fetch notes from server - done
 *
 * Todo: 5. upload user image - NOT DONE
 *
 * Todo: 6. add refresh functionality - Done
 *
 * TODO: 7. Make theme dark and white best way
 *
 */
