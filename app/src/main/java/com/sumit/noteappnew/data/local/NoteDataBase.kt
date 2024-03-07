package com.sumit.noteappnew.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sumit.noteappnew.data.local.dao.NoteDao
import com.sumit.noteappnew.data.local.models.LocalNote

@Database(entities = [LocalNote::class], version = 1, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao


}