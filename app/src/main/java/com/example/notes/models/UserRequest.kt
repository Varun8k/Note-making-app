package com.example.notes.models

data class UserRequest(
    val username: String,
    val email: String,
    val password: String
)