package com.sumit.noteappnew.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumit.noteappnew.data.local.models.LocalNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LocalNote)

    @Query("SELECT * FROM LocalNote WHERE isLocallyDeleted = 0 ORDER BY date DESC")
    fun getAllNotesOrderedByDate(): Flow<List<LocalNote>>

    @Query("DELETE FROM LocalNote WHERE id=:noteId")
    suspend fun deleteNote(noteId: String)

    @Query("UPDATE LocalNote SET isLocallyDeleted = 1 WHERE id=:noteId")
    suspend fun deleteNoteLocally(noteId: String)

    @Query("SELECT * FROM LocalNote WHERE isConnected = 0")
    suspend fun getAllLocalDeletedNotes(): List<LocalNote>

    @Query("SELECT * FROM LocalNote WHERE isLocallyDeleted = 1")
    suspend fun getAllLocallyNotes(): List<LocalNote>
}