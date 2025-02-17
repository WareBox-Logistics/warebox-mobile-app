package com.example.lp_logistics.presentation.screens.profile

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _roleName = mutableStateOf("")
    val roleName: State<String> = _roleName

    fun logout(context: Context, navController: NavController){
        viewModelScope.launch {
            try{
                val token = UserManager.getToken(context).toString()
                authRepository.logout(token)
                UserManager.clearUser(context)
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true } // Remove all previous screens from stack
                }
            }catch (e: Exception){
                println("Logout failed: ${e.message}")
            }
        }
    }


}