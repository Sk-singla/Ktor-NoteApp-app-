package com.example.mynotes.ui

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentAllNotesBinding
import com.example.mynotes.ui.adapters.NotesAdapter
import com.example.mynotes.ui.viewmodels.NoteViewModel
import com.example.mynotes.utils.Result
import com.example.mynotes.utils.startAnimation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class AllNotesFragment:Fragment(R.layout.fragment_all_notes) {

    private lateinit var binding: FragmentAllNotesBinding
    private lateinit var noteViewModel:NoteViewModel
    private lateinit var noteAdapter:NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllNotesBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.customToolBar)

        noteViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)
        noteAdapter = NotesAdapter()

        setUpRecyclerView()
        subscribeToNotes()
        setSwipeLayout()


        // New Note

//        val animation = AnimationUtils.loadAnimation(requireContext(),R.anim.scale_animation).apply {
//            duration = 700
//            interpolator = AccelerateDecelerateInterpolator()
//        }
        binding.newNoteFab.setOnClickListener {
//            binding.newNoteFab.isVisible = false
//            binding.circle.isVisible = true
//            binding.circle.startAnimation(animation){
//                binding.circle.isVisible = false
                findNavController().navigate(R.id.action_allNotesFragment_to_newNoteFragment)
//            }
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        val item = menu.findItem(R.id.search)
        val search = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                subscribeToNotes()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                subscribeToNotes()
                return true
            }
        })

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { subscribeToNotes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { subscribeToNotes(it) }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.account->{
                findNavController().navigate(R.id.action_allNotesFragment_to_userFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView(){
        noteAdapter.setOnItemClikcListener { note,titleView,descView->
            val action = AllNotesFragmentDirections.actionAllNotesFragmentToNewNoteFragment(note)
//            val extras = FragmentNavigatorExtras(
//                    titleView to resources.getString(R.string.transition_title),
//                    descView to resources.getString(R.string.transition_description)
//            )
            findNavController().navigate(action)
        }
        binding.noteRecyclerView.adapter = noteAdapter
        binding.noteRecyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        ItemTouchHelper(itemTouchHelper)
            .attachToRecyclerView(binding.noteRecyclerView)
    }

    private fun subscribeToNotes(query:String = ""){
        noteViewModel.notes.observe(viewLifecycleOwner){
            it?.let { notes->
                noteAdapter.notesList = notes.filter { note->
                    note.noteTitle?.let{ title->
                        title.contains(query)
                    } == true || note.description?.let { description->
                        description.contains(query)
                    } == true
                }
            }
        }
    }

    private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notesList[position]
            viewHolder.itemView.alpha = 0F
            noteViewModel.deleteNote(note)
            Snackbar.make(requireView(),"Note Deleted Successfully",Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    noteViewModel.addNote(note.apply { deleteLocal = false })
                }
                show()
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX/2, dY, actionState, isCurrentlyActive)

        }
    }

    private fun setSwipeLayout(){

        binding.swipeRefeeshLayout.apply {
            setOnRefreshListener {
                noteViewModel.syncNotes {
                    this.isRefreshing = false
                }
            }
        }
    }


}