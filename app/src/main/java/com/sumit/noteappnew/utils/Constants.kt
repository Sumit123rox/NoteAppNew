package com.sumit.noteappnew.utils

object Constants {
    const val SESSION_MANAGER = "session_manager"
    const val JWT_TOKEN_KEY = "jwt_token_key"
    const val USERNAME = "username"
    const val PASSWORD = "password"
    const val EMAIL = "email"
    const val NOTE_DB_NAME = "note_db"
    const val AUTHORIZATION = "Authorization"

    //API URLs
    const val BASE_URL = "https://noteapp-v4tk.onrender.com"
    const val API_VERSION = "/v1"

    //Register and Login
    const val REGISTER_URL = "$BASE_URL$API_VERSION/users/register"
    const val LOGIN_URL = "$BASE_URL$API_VERSION/users/login"

    //Note URLs
    const val NOTES_URL = "$BASE_URL$API_VERSION/notes"
    const val CREATE_NOTE_URL = "$NOTES_URL/createNote"
    const val UPDATE_NOTE_URL = "$NOTES_URL/updateNote"
    const val DELETE_NOTE_URL = "$NOTES_URL/deleteNote"

    const val TIME_OUT = 10_000
    const val TAG_KTOR_LOGGER = "ktor_logger:"
    const val TAG_HTTP_STATUS_LOGGER = "http_status:"

    const val MINIMUM_PASSWORD_LENGTH = 4
    const val MAXIMUM_PASSWORD_LENGTH = 8
}