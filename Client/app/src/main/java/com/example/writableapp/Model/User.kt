package com.example.writableapp.Model

class User(
    private val uid: String,
    private val display_name: String,
    private val avatar_url: String,
    private val email: String,
    private val password: String
) {
    fun getUID(): String {
        return this.uid
    }

    fun getDisplayName(): String {
        return this.display_name
    }

    fun getAvatarURL(): String {
        return this.avatar_url
    }

    fun getEmail(): String {
        return this.email
    }

    fun getPassword(): String {
        return this.password
    }
}