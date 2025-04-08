package com.example.lp_logistics.presentation.screens.login

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.repository.AuthRepository
import com.example.lp_logistics.data.repository.CredentialsException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError


    fun loginEmployee(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            try {
                val response = authRepository.loginEmployee(email, password)

                UserManager.saveUser(context, response.user, response.token)

                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            authRepository.updateFcmToken(
                                userId = response.user.id,
                                token = task.result
                            )
                        }
                    }
                }

                _isLoggedIn.value = true
            } catch (e: CredentialsException) {
                // Show the exact credential error message
                _loginError.value = e.message
                _isLoggedIn.value = false
            } catch (e: SecurityException) {
                _loginError.value = "Your role doesn't have access to this app"
                _isLoggedIn.value = false
            } catch (e: Exception) {
                // Fallback for other errors
                _loginError.value = when {
                    e.message?.contains("HTTP 401") == true -> "The provided credentials are incorrect"
                    else -> "Login failed. Please try again later."
                }
                _isLoggedIn.value = false
            }
        }
    }
}