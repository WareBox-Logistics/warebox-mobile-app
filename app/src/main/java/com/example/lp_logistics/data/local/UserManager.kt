package com.example.lp_logistics.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lp_logistics.data.remote.requests.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object UserManager {
    private val TOKEN_KEY = stringPreferencesKey("session_token")
    private val USER_KEY = stringPreferencesKey("user_data")
    private val gson = Gson()

    // Save User and Token
    suspend fun saveUser(context: Context, user: User, token: String) {
        val userJson = gson.toJson(user) // Convert User object to JSON string
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_KEY] = userJson
        }
    }

    // Get Token
    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { prefs -> prefs[TOKEN_KEY] }.first()
    }

    // Get User Object
    suspend fun getUser(context: Context): User? {
        val userJson = context.dataStore.data.map { prefs -> prefs[USER_KEY] }.first()
        return userJson?.let { gson.fromJson(it, User::class.java) } // Convert JSON back to User object
    }

    // Clear User Data on Logout
    suspend fun clearUser(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_KEY)
        }
    }
}
