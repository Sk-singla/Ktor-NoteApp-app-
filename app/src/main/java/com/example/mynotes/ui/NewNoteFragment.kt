package com.example.mynotes.ui

import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.data.local.model.NoteLocal
import com.example.mynotes.databinding.FragmentNewNoteBinding
import com.example.mynotes.ui.viewmodels.NoteViewModel
import com.example.mynotes.utils.millToDate


class NewNoteFragment:Fragment(R.layout.fragment_new_note) {

    private lateinit var binding: FragmentNewNoteBinding
    private lateinit var noteViewModel:NoteViewModel

    private val args:NewNoteFragmentArgs by navArgs()
    private var currentNote:NoteLocal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        sharedElementEnterTransition = ChangeBounds().apply {
//            duration = 750
//        }
//        sharedElementReturnTransition = ChangeBounds().apply {
//            duration = 750
//        }
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewNoteBinding.bind(view)
        noteViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)

        currentNote = args.note
        currentNote?.let{
            binding.newNoteTitleEditText.setText(it.noteTitle)
            binding.newNoteDescriptionEditText.setText(it.description)
            binding.date.visibility = View.VISIBLE
            binding.date.text = millToDate(it.date)

            binding.newNoteTitleEditText.clearFocus()
            binding.newNoteDescriptionEditText.clearFocus()
        }

        if(currentNote==null){
            if(binding.newNoteDescriptionEditText.requestFocus()){
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }


    }

    override fun onPause() {
        super.onPause()
        if(currentNote==null){
            addNote()
        } else {
            updateNote()
        }
    }

    private fun updateNote(){
        val currentTime = System.currentTimeMillis()
        val title = binding.newNoteTitleEditText.text.toString().trim()
        val description = binding.newNoteDescriptionEditText.text.toString().trim()
        val note = NoteLocal(
                title,
                description,
                currentTime
        )

        if(title == currentNote?.noteTitle && description == currentNote?.description){
            return
        }
        note.noteId = currentNote!!.noteId

        noteViewModel.updateNote(note)
        binding.newNoteTitleEditText.text.clear()
        binding.newNoteDescriptionEditText.text.clear()

    }

    private fun addNote(){
        val currentTime = System.currentTimeMillis()
        val title = binding.newNoteTitleEditText.text.toString().trim()
        val description = binding.newNoteDescriptionEditText.text.toString().trim()
        val note = NoteLocal(
                title,
                description,
                currentTime
        )


        if(title.isEmpty() && description.isEmpty()){
            Toast.makeText(requireContext(), "Empty Note discarded", Toast.LENGTH_SHORT).show()
            return
        }

        noteViewModel.addNote(note)
        binding.newNoteTitleEditText.text.clear()
        binding.newNoteDescriptionEditText.text.clear()

    }

}