package com.sumit.noteappnew.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumit.noteappnew.data.local.models.LocalNote
import com.sumit.noteappnew.repository.NoteRepository
import com.sumit.noteappnew.utils.loge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    var oldNote: LocalNote? = null
    val notes = repository.getAllNotes()
    var searchQuery = ""

    fun syncNotes(
        onDone: (() -> Unit)? = null
    ) = viewModelScope.launch {
        repository.syncNotes()
        onDone?.invoke()
    }

    fun createNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val localNote = LocalNote(
            noteTitle = noteTitle,
            description = description
        )

        repository.createNote(localNote)
    }

    fun updateNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {

        loge { "noteTitle == oldNote?.noteTitle && description == oldNote?.description && oldNote?.isConnected == true: ${noteTitle == oldNote?.noteTitle && description == oldNote?.description && oldNote?.isConnected == true}" }

        if (noteTitle == oldNote?.noteTitle && description == oldNote?.description && oldNote?.isConnected == true) {
            return@launch
        }

        val note = LocalNote(
            noteTitle = noteTitle,
            description = description,
            id = oldNote?.id!!
        )

        repository.updateNote(note)
    }

    fun deleteNote(id: String) = viewModelScope.launch {
        repository.deleteNote(id)
    }

    fun undoDelete(note: LocalNote) = viewModelScope.launch {
        repository.createNote(note)
    }

}