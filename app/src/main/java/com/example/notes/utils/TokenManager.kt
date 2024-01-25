package com.example.notes.utils

import android.content.Context
import com.example.notes.utils.Constant.PREF_NAME
import com.example.notes.utils.Constant.USER_AUTH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    fun saveToken(token: String) {
        val preferences = preferences.edit()
            .putString(USER_AUTH_TOKEN, token)
        preferences.apply()
    }
    fun getToken(): String? {
        return preferences.getString(USER_AUTH_TOKEN, null)
    }

    fun deleteToken() {
        val editor = preferences.edit()
        editor.remove(USER_AUTH_TOKEN)
        editor.apply()
    }
}
