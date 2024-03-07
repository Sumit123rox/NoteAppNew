package com.sumit.noteappnew.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.sumit.noteappnew.data.local.NoteDataBase
import com.sumit.noteappnew.data.local.dao.NoteDao
import com.sumit.noteappnew.repository.NoteRepository
import com.sumit.noteappnew.repository.NoteRepositoryImp
import com.sumit.noteappnew.utils.Constants.NOTE_DB_NAME
import com.sumit.noteappnew.utils.Constants.TAG_HTTP_STATUS_LOGGER
import com.sumit.noteappnew.utils.Constants.TAG_KTOR_LOGGER
import com.sumit.noteappnew.utils.Constants.TIME_OUT
import com.sumit.noteappnew.utils.SessionManager
import com.sumit.noteappnew.utils.loge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

    @Provides
    @Singleton
    fun provideNoteDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        NoteDataBase::class.java,
        NOTE_DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideNoteDao(
        noteDataBase: NoteDataBase
    ) = noteDataBase.getNoteDao()

    @Provides
    @Singleton
    fun provideKtorHttpClient(): HttpClient {

        val serializeJson = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(json = serializeJson)
                engine {
                    connectTimeout = TIME_OUT
                    socketTimeout = TIME_OUT
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        loge { message }
                    }
                }
                level = LogLevel.ALL
            }

            install(ResponseObserver) {
                onResponse { response ->
                    loge { "${response.status.value}" }
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        httpClient: HttpClient,
        noteDao: NoteDao,
        sessionManager: SessionManager
    ): NoteRepository = NoteRepositoryImp(httpClient, noteDao, sessionManager)
}