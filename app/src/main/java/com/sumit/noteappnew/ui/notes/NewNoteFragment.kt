package com.sumit.noteappnew.ui.notes

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.sumit.noteappnew.R
import com.sumit.noteappnew.databinding.FragmentNewNoteBinding
import com.sumit.noteappnew.ui.BaseFragment
import com.sumit.noteappnew.utils.loge
import com.sumit.noteappnew.utils.millisInDate
import com.sumit.noteappnew.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNoteFragment : BaseFragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding: FragmentNewNoteBinding?
        get() = _binding

    private val args by navArgs<NewNoteFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewNoteBinding.bind(view)

        noteViewModel.oldNote = args.note

        noteViewModel.oldNote?.noteTitle?.let {
            binding?.newNoteTitleEditText?.setText(it)
        }

        noteViewModel.oldNote?.description?.let {
            binding?.newNoteDescriptionEditText?.setText(it)
        }

        binding?.date?.isVisible = noteViewModel.oldNote != null

        noteViewModel.oldNote?.date?.let {
            binding?.date?.text = it.millisInDate()
        }
    }

    override fun onPause() {
        super.onPause()

        loge { "oldNote: ${noteViewModel.oldNote}" }

        if (noteViewModel.oldNote == null) {
            createNote()
        } else {
            updateNote()
        }
    }

    private fun createNote() {
        val noteTitle = binding?.newNoteTitleEditText?.text.toString().trim()
        val description = binding?.newNoteDescriptionEditText?.text.toString().trim()

        if (noteTitle.isEmpty() || description.isEmpty()) {
            mContext.toast { "Note is Empty" }
            return
        }

        noteViewModel.createNote(noteTitle, description)
    }

    private fun updateNote() {
        val noteTitle = binding?.newNoteTitleEditText?.text.toString().trim()
        val description = binding?.newNoteDescriptionEditText?.text.toString().trim()

        loge { "noteTitle: $noteTitle\ndescription: $description" }

        if (noteTitle.isEmpty() || description.isEmpty()) {
            noteViewModel.deleteNote(noteViewModel.oldNote?.id!!)
            return
        }

        noteViewModel.updateNote(noteTitle, description)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewNoteFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}