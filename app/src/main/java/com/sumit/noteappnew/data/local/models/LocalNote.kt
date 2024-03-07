package com.sumit.noteappnew.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class LocalNote(
    @PrimaryKey(false)
    var id: String = UUID.randomUUID().toString(),
    var noteTitle: String? = null,
    var description: String? = null,
    var date: Long = System.currentTimeMillis(),
    var isConnected: Boolean = false,
    var isLocallyDeleted: Boolean = false
) : Serializable
