package com.example.notes.utils

import android.text.TextUtils
import android.util.Patterns

class ValidateUserInput {
    fun validate(username: String, email: String, password: String, isLogin: Boolean): Pair<Boolean, String> {
        var result = Pair(true, "")

        when {
            !isLogin && (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) ->
                result = Pair(false, "Please provide all required credentials")

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                result = Pair(false, "Please provide a valid email address")

            !isLogin && password.length <= 5 ->
                result = Pair(false, "Password is too short")
        }

        return result
    }
}
