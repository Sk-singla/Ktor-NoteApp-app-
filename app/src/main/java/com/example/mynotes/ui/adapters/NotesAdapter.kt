package com.example.mynotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.data.local.model.NoteLocal
import com.example.mynotes.databinding.ItemNoteBinding
import com.example.mynotes.utils.millToDate
import org.w3c.dom.Text

class NotesAdapter():RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(val itemBinding:ItemNoteBinding):RecyclerView.ViewHolder(itemBinding.root)

    val diffUtil = object :DiffUtil.ItemCallback<NoteLocal>(){
        override fun areItemsTheSame(oldItem: NoteLocal, newItem: NoteLocal): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: NoteLocal, newItem: NoteLocal): Boolean {
            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this,diffUtil)
    var notesList:List<NoteLocal>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                ItemNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notesList[position]
        holder.itemBinding.apply {
            if(note.noteTitle?.isNotEmpty() == true) {
                noteText.visibility = View.VISIBLE
                noteText.text = note.noteTitle
            }
            else{
                noteText.visibility = View.GONE
            }

            if(note.description?.isNotEmpty() == true) {
                noteDescription.visibility = View.VISIBLE
                noteDescription.text = note.description
            } else {
                noteDescription.visibility = View.GONE
            }
            if(note.connected){
                noteSync.setBackgroundResource(R.drawable.synced)
            } else {
                noteSync.setBackgroundResource(R.drawable.not_sync)
            }

            root.setOnClickListener {
                onItemClickListener?.let { it ->
                    it(note,noteText,noteDescription)
                }
            }
        }
    }

    private var onItemClickListener:((note:NoteLocal,titleView:TextView,descView:TextView)->Unit)? =null
    fun setOnItemClikcListener(listener:(note:NoteLocal,titleView:TextView,descView:TextView)->Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

}