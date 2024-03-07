package com.sumit.noteappnew.ui.notes

import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sumit.noteappnew.R
import com.sumit.noteappnew.databinding.FragmentAllNotesBinding
import com.sumit.noteappnew.ui.BaseFragment
import com.sumit.noteappnew.ui.adapter.NoteAdapter
import com.sumit.noteappnew.utils.addMenuProvider
import com.sumit.noteappnew.utils.getActivity
import com.sumit.noteappnew.utils.loge
import com.sumit.noteappnew.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllNotesFragment : BaseFragment(R.layout.fragment_all_notes) {

    private var _binding: FragmentAllNotesBinding? = null
    private val binding: FragmentAllNotesBinding?
        get() = _binding
    private lateinit var noteAdapter: NoteAdapter

    private val itemTouchHelperCallbacl = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            noteViewModel.deleteNote(note.id)
            binding?.root?.snackBar("Note Deleted SuccessFully") {
                setAction("Undo") {
                    noteViewModel.undoDelete(note)
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
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX / 2,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllNotesBinding.bind(view)

        mContext.getActivity()?.setSupportActionBar(binding?.customToolBar)

        setupSwipeRefresh()
        setupMainMenu()
        setupListeners()
        setupRecyclerview()
        subscribeToNote()
        noteViewModel.syncNotes()
    }

    private fun setupMainMenu() {
        addMenuProvider(mContext, R.menu.main_menu, onMenuCreate = { menu, menuInflater ->
            setupSearch(menu)
        }) {

            when (it.itemId) {
                R.id.account -> {
                    findNavController().navigate(R.id.action_allNotesFragment_to_userInfoFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupListeners() {
        with(binding!!) {
            newNoteFab.setOnClickListener {
                findNavController().navigate(R.id.action_allNotesFragment_to_newNoteFragment)
            }
        }
    }

    private fun setupRecyclerview() {
        noteAdapter = NoteAdapter {
            loge { "Note: $this" }
            val action =
                AllNotesFragmentDirections.actionAllNotesFragmentToNewNoteFragment(this)
            findNavController().navigate(action)
        }

        binding?.noteRecyclerView?.apply {
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            ItemTouchHelper(itemTouchHelperCallbacl).attachToRecyclerView(this)
        }
    }

    private fun subscribeToNote() = lifecycleScope.launch {
        noteViewModel.notes.collectLatest {
            noteAdapter.notes = it.filter { localeNote ->
                localeNote.noteTitle?.contains(noteViewModel.searchQuery, true) == true ||
                        localeNote.description?.contains(noteViewModel.searchQuery, true) == true
            }
        }
    }

    private fun setupSearch(menu: Menu) {
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView

        item.setOnActionExpandListener(object : OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                noteViewModel.searchQuery = ""
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                noteViewModel.searchQuery = ""
                return true
            }
        })

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchNotes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchNotes(it)
                }
                return true
            }
        })
    }

    private fun searchNotes(query: String) = lifecycleScope.launch {
        noteViewModel.searchQuery = query
        noteAdapter.notes = noteViewModel.notes.first().filter {
            it.noteTitle?.contains(query, true) == true ||
                    it.description?.contains(query, true) == true
        }
    }

    private fun setupSwipeRefresh() {
        binding?.swipeRefeeshLayout?.setOnRefreshListener {
            noteViewModel.syncNotes {
                binding?.swipeRefeeshLayout?.isRefreshing = false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AllNotesFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}