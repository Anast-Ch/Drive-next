package com.example.myproject.data.local

import android.content.Context


class AuthPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun saveSession(token: String, email: String? = null) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EMAIL = "auth_email"
    }
}