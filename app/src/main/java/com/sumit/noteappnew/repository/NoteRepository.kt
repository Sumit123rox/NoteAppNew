package com.sumit.noteappnew.repository

import com.sumit.noteappnew.data.local.models.LocalNote
import com.sumit.noteappnew.data.remote.models.RemoteNote
import com.sumit.noteappnew.data.remote.models.SimpleResponse
import com.sumit.noteappnew.data.remote.models.User
import com.sumit.noteappnew.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    //Register & Login
    suspend fun createAccount(user: User): Resource<SimpleResponse>
    suspend fun login(user: User): Resource<SimpleResponse>
    suspend fun getUser(): Resource<SimpleResponse>
    suspend fun logout(): Resource<String>

    //==============Note APIs=====================
    //Create Note
    suspend fun createNote(note: LocalNote): Resource<String>
    //Get All Notes From Local DB
    fun getAllNotes(): Flow<List<LocalNote>>
    suspend fun getAllNotesFromServer()
    //Update the Note
    suspend fun updateNote(note: LocalNote): Resource<String>
    //Delete the Note
    suspend fun deleteNote(id: String): Resource<SimpleResponse>
   suspend fun syncNotes()

}