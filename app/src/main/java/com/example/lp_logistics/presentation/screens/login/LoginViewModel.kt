package com.example.lp_logistics.presentation.screens.login

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor( private val authRepository: AuthRepository) : ViewModel( ){
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    fun loginEmployee(context: Context, email: String, password: String){
        viewModelScope.launch {
            try{
                val response = authRepository.loginEmployee(email, password)
                //save our token securely
                UserManager.saveUser(context, response.user, response.token)
                _isLoggedIn.value = true
                println("Login successful: ${response.user.first_name} , ${response.token}")
            }catch (e: Exception){
                println("Login failed: ${ e.message }")
                _isLoggedIn.value = false
            }
        }
    }

}