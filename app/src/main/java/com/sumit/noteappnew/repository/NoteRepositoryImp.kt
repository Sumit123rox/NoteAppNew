package com.sumit.noteappnew.repository

import com.sumit.noteappnew.data.local.dao.NoteDao
import com.sumit.noteappnew.data.local.models.LocalNote
import com.sumit.noteappnew.data.remote.models.RemoteNote
import com.sumit.noteappnew.data.remote.models.SimpleResponse
import com.sumit.noteappnew.data.remote.models.User
import com.sumit.noteappnew.utils.Constants.CREATE_NOTE_URL
import com.sumit.noteappnew.utils.Constants.DELETE_NOTE_URL
import com.sumit.noteappnew.utils.Constants.EMAIL
import com.sumit.noteappnew.utils.Constants.JWT_TOKEN_KEY
import com.sumit.noteappnew.utils.Constants.LOGIN_URL
import com.sumit.noteappnew.utils.Constants.NOTES_URL
import com.sumit.noteappnew.utils.Constants.REGISTER_URL
import com.sumit.noteappnew.utils.Constants.UPDATE_NOTE_URL
import com.sumit.noteappnew.utils.Constants.USERNAME
import com.sumit.noteappnew.utils.Resource
import com.sumit.noteappnew.utils.SessionManager
import com.sumit.noteappnew.utils.isNetworkConnected
import com.sumit.noteappnew.utils.loge
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
    private val client: HttpClient,
    private val noteDao: NoteDao,
    private val sessionManager: SessionManager
) : NoteRepository {

    override suspend fun createAccount(user: User): Resource<SimpleResponse> =
        try {
            if (!sessionManager.context.isNetworkConnected()) {
                Resource.Failure("No Internet Connection")
            }

            val response = client.post(REGISTER_URL) {
                setBody(user)
            }.body<SimpleResponse>()

            if (response.success) {
                getAllNotesFromServer()
                Resource.Success(response)
            } else {
                Resource.Failure(response.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e.message ?: "Error to Register the User")
        }

    override suspend fun login(user: User): Resource<SimpleResponse> = try {
        if (!sessionManager.context.isNetworkConnected()) {
            Resource.Failure(errorMessage = "No Internet Connection")
        }

        Resource.Success(
            client.post(LOGIN_URL) {
                setBody(user)
            }.body<SimpleResponse>(),
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Failure(errorMessage = e.message ?: "Login failed")
    }

    override suspend fun getUser(): Resource<SimpleResponse> = try {
        val name = sessionManager.getString(USERNAME)
        val email = sessionManager.getString(EMAIL)

        loge { "Username: $name email: $email" }

        if (name.isNullOrEmpty() || email.isNullOrEmpty())
            Resource.Failure("User is not logged In")

        Resource.Success(SimpleResponse(true, "User found", User(name, email!!, "")))
    } catch (e: Exception) {
        Resource.Failure(e.message ?: "Some error occurred")
    }

    override suspend fun logout(): Resource<String> = try {
        sessionManager.logout()
        Resource.Success("User Successfully logged out")
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Failure("Error to logout the User")
    }

    override suspend fun createNote(note: LocalNote): Resource<String> {
        try {
            noteDao.insertNote(note = note)
            if (!sessionManager.context.isNetworkConnected()) {
                Resource.Failure(errorMessage = "No Internet Connection")
            }
            val token = sessionManager.getString(JWT_TOKEN_KEY)
                ?: return Resource.Success("Note is saved successfully in local Database.")


            val response =
                client.post(CREATE_NOTE_URL) {
                    bearerAuth(token)
                    setBody(
                        RemoteNote(
                            id = note.id,
                            noteTitle = note.noteTitle,
                            description = note.description,
                            date = note.date
                        )
                    )
                }.body<SimpleResponse>()

            return if (response.success) {
                noteDao.insertNote(note.also { it.isConnected = true })
                Resource.Success("Note Saved Successfully")
            } else {
                Resource.Failure(response.message)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Failure(e.message ?: "Some error Occurred!")
        }
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotesOrderedByDate()

    override suspend fun getAllNotesFromServer() {
        try {
            if (!sessionManager.context.isNetworkConnected()) {
                return /*Resource.Failure(errorMessage = "No Internet Connection")*/
            }

            val token = sessionManager.getString(JWT_TOKEN_KEY)
                ?: return /*Resource.Failure(errorMessage = "Error to get Token")*/

            val response = client.get(NOTES_URL) {
                bearerAuth(token)
            }.body<List<RemoteNote>>()

            response.forEach { remoteNote ->
                noteDao.insertNote(
                    LocalNote(
                        id = remoteNote.id,
                        noteTitle = remoteNote.noteTitle,
                        description = remoteNote.description,
                        date = remoteNote.date,
                        isConnected = true
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateNote(note: LocalNote): Resource<String> {
        try {
            noteDao.insertNote(note = note)
            if (!sessionManager.context.isNetworkConnected()) {
                Resource.Failure(errorMessage = "No Internet Connection")
            }

            val token = sessionManager.getString(JWT_TOKEN_KEY)
                ?: return Resource.Success("Note is updated successfully in local Database.")

            val response =
                client.post(UPDATE_NOTE_URL) {
                    bearerAuth(token)
                    setBody(
                        RemoteNote(
                            id = note.id,
                            noteTitle = note.noteTitle,
                            description = note.description,
                            date = note.date
                        )
                    )
                }.body<SimpleResponse>()

            return if (response.success) {
                noteDao.insertNote(note.also { it.isConnected = true })
                Resource.Success("Note Updated Successfully")
            } else {
                Resource.Failure(response.message)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Failure(e.message ?: "Some error Occurred!")
        }
    }

    override suspend fun deleteNote(id: String): Resource<SimpleResponse> {
        try {
            noteDao.deleteNoteLocally(id)

            val token = sessionManager.getString(JWT_TOKEN_KEY)
                ?: run {
                    noteDao.deleteNote(id)
                    return Resource.Failure("Note is updated successfully in local Database.")
                }

            if (!sessionManager.context.isNetworkConnected()) {
                return Resource.Failure(errorMessage = "No Internet Connection")
            }

            val response = client.post(DELETE_NOTE_URL) {
                bearerAuth(token)
                parameter("id", id)
            }.body<SimpleResponse>()

            if (response.success) {
                noteDao.deleteNote(id)
            }

            return Resource.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Failure("")
        }
    }

    override suspend fun syncNotes() {
        try {
            val token = sessionManager.getString(JWT_TOKEN_KEY)

            if (!sessionManager.context.isNetworkConnected()) {
                return
            }

            val locallyDeletedNotes = noteDao.getAllLocalDeletedNotes()
            locallyDeletedNotes.forEach {
                deleteNote(it.id)
            }

            val notConnectedNotes = noteDao.getAllLocallyNotes()
            notConnectedNotes.forEach {
                createNote(it)
            }

            val notUpdatedNotes = noteDao.getAllLocallyNotes()
            notUpdatedNotes.forEach {
                updateNote(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}