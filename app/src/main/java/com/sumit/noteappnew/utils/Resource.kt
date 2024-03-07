package com.sumit.noteappnew.utils

sealed class Resource<out R> {
    data class Success<out R>(val result: R): Resource<R>()
    data class Failure(val errorMessage: String): Resource<Nothing>()
    data object Loading: Resource<Nothing>()
}