package com.sumit.noteappnew.data.remote.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String? = null,
    val email: String,
    @SerialName("password")
    val hashPassword: String
)
