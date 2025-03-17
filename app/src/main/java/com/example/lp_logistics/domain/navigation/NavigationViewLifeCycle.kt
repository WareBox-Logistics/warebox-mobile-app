package com.example.lp_logistics.domain.navigation
//
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.LifecycleOwner
//import com.google.android.libraries.navigation.NavigationView
//
//abstract class NavigationViewLifecycleObserver(
//    private val navigationView: NavigationView,
//    private val lifecycleOwner: LifecycleOwner
//) : LifecycleEventObserver {
//
//    init {
//        lifecycleOwner.lifecycle.addObserver(this)
//    }
//
//    @androidx.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun onResume() {
//        navigationView.onResume()
//    }
//
//    @androidx.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun onPause() {
//        navigationView.onPause()
//    }
//
//    @androidx.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        navigationView.onDestroy()
//        lifecycleOwner.lifecycle.removeObserver(this)
//    }
//}