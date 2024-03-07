package com.sumit.noteappnew.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumit.noteappnew.data.remote.models.SimpleResponse
import com.sumit.noteappnew.data.remote.models.User
import com.sumit.noteappnew.repository.NoteRepository
import com.sumit.noteappnew.utils.Constants.MAXIMUM_PASSWORD_LENGTH
import com.sumit.noteappnew.utils.Constants.MINIMUM_PASSWORD_LENGTH
import com.sumit.noteappnew.utils.Resource
import com.sumit.noteappnew.utils.loge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _registerState: MutableSharedFlow<Resource<SimpleResponse>> = MutableSharedFlow()
    var registerState: SharedFlow<Resource<SimpleResponse>> = _registerState

    private val _loginState: MutableSharedFlow<Resource<SimpleResponse>> = MutableSharedFlow()
    var loginState: SharedFlow<Resource<SimpleResponse>> = _loginState

    private val _currentUserState: MutableSharedFlow<Resource<SimpleResponse>> = MutableSharedFlow()
    var currentUserState: SharedFlow<Resource<SimpleResponse>> = _currentUserState

    fun createUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        _registerState.emit(Resource.Loading)

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || password != confirmPassword) {
            _registerState.emit(Resource.Failure("Some fields are empty"))
            return@launch
        }

        if (!isEmailValid(email)) {
            _registerState.emit(Resource.Failure("Email is not valid"))
            return@launch
        }

        if (!isPasswordValid(password)) {
            _registerState.emit(Resource.Failure("Password should be between $MINIMUM_PASSWORD_LENGTH and $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val user = User(name, email, password)

        _registerState.emit(repository.createAccount(user))
    }

    fun loginUser(
        name: String,
        email: String,
        password: String,
    ) = viewModelScope.launch {
        _loginState.emit(Resource.Loading)

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _loginState.emit(Resource.Failure("Some fields are empty"))
            return@launch
        }

        if (!isEmailValid(email)) {
            _loginState.emit(Resource.Failure("Email is not valid"))
            return@launch
        }

        if (!isPasswordValid(password)) {
            _loginState.emit(Resource.Failure("Password should be between $MINIMUM_PASSWORD_LENGTH and $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val user = User(name, email, password)

        loge { "Repository Login Called" }
        _loginState.emit(repository.login(user))
    }

    fun getCurrentUser() = viewModelScope.launch {
        _currentUserState.emit(Resource.Loading)
        _currentUserState.emit(repository.getUser())
    }

    fun logout() = viewModelScope.launch {
        val logout = repository.logout()
        if (logout is Resource.Success) {
            getCurrentUser()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val regex =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        val pattern = Pattern.compile(regex)
        return (email.isNotEmpty() && pattern.matcher(email).matches())
    }

    private fun isPasswordValid(password: String): Boolean =
        (password.length in MINIMUM_PASSWORD_LENGTH..MAXIMUM_PASSWORD_LENGTH)
}