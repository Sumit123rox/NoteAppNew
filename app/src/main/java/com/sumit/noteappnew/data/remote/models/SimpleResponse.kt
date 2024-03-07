package com.sumit.noteappnew.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)
