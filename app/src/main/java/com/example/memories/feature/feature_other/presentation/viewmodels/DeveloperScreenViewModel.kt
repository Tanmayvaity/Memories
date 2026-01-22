package com.example.memories.feature.feature_other.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_other.domain.model.GithubUserInfo
import com.example.memories.feature.feature_other.domain.repository.RemoteUserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class DeveloperScreenViewModel @Inject constructor(
    private val githubService: RemoteUserService
) : ViewModel() {
    companion object {
        const val TAG = "DeveloperScreenViewModel"
    }



    private val _state = MutableStateFlow<DeveloperInfoState>(DeveloperInfoState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<DeveloperScreenEvent>(replay = 1)
    val events = _events.asSharedFlow()


    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) } // Clear previous error

            try {
                val user = githubService.getUserInfo()
                Log.d(TAG, "fetchUser: $user")
                _state.update { it.copy(user = user, isLoading = false, error = null) }

            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is UnknownHostException -> handleError("No internet connection", e)
                    is SocketTimeoutException -> handleError("Connection timed out", e)
                    else -> handleError("Something went wrong", e)
                }
            }
        }
    }

    private suspend  fun handleError(message: String, e: Exception) {
        Log.e(TAG, "$message: $e")
        _state.update { it.copy(error = message, isLoading = false) }
        _events.emit(DeveloperScreenEvent.Error(message))
    }

}

data class DeveloperInfoState(
    val user: GithubUserInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

 sealed interface DeveloperScreenEvent {
    data class Error(val message: String) : DeveloperScreenEvent
}