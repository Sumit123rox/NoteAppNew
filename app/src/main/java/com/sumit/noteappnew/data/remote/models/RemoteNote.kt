package com.sumit.noteappnew.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteNote(
    val id: String,
    val noteTitle: String?,
    val description: String?,
    val date: Long,
)