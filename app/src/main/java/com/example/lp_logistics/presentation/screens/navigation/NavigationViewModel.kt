package com.example.lp_logistics.presentation.screens.navigation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.repository.NavigationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(private val navigationRepository: NavigationRepository) : ViewModel() {

    private val _route = MutableStateFlow<List<LatLng>>(emptyList())
    val route: StateFlow<List<LatLng>> = _route

    private val _routeName = MutableStateFlow("")
    val routeName: StateFlow<String> = _routeName


}

