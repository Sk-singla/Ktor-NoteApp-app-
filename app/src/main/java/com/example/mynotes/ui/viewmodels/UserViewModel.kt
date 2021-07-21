package com.example.mynotes.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.remote.model.User
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
        val repo: NoteRepository
):ViewModel() {

    private val _currentUser = MutableLiveData<Result<User>>()
    val currentUser:LiveData<Result<User>> = _currentUser

    private val _userToken = MutableLiveData<Result<String>>()
    val userToken:LiveData<Result<String>> = _userToken

    private val _userEvent = Channel<Result<User>>()
    val userEvent = _userEvent.receiveAsFlow()

    fun signUp(user: User) = viewModelScope.launch{
        _userToken.postValue(Result.Loading())
        val result = repo.createUser(user)
        _userToken.postValue(result)
    }

    fun getCurrentUser(action:(()->Unit)?=null) = viewModelScope.launch {
        _userEvent.send(Result.Loading())
        _userEvent.send(repo.findCurUser())

//        _currentUser.postValue(Result.Loading())
//        val result = repo.findCurUser()
//        _currentUser.postValue(result)

        action?.let {
            it.invoke()
        }
    }

    fun loginUser(user:User) = viewModelScope.launch {
        _userToken.postValue(Result.Loading())
        _userToken.postValue(repo.loginUser(user))
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
        _currentUser.postValue(repo.findCurUser())
    }



}