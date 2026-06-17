package com.example.expensetracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_MOBILE = stringPreferencesKey("user_mobile")
        private val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "John Doe"
    }
    val userMobile: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_MOBILE] ?: "9876543210"
    }
    val userPhotoUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_PHOTO_URI]
    }

    suspend fun saveProfile(name: String, mobile: String, photoUri: String?) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_MOBILE] = mobile
            if (photoUri != null) {
                preferences[USER_PHOTO_URI] = photoUri
            } else {
                preferences.remove(USER_PHOTO_URI)
            }
        }
    }
}
