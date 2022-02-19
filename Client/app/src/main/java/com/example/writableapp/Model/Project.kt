package com.example.writableapp.Model

class Project(
    private val pid: String,
    private val uid: String,
    private val title: String,
    private val imageURL: String,
    private val description: String,
    private val wordCount: String,
    private val completed: String
) {
    fun getPID(): String {
        return this.pid
    }

    fun getUID(): String {
        return this.uid
    }

    fun getTitle(): String {
        return this.title
    }

    fun getImageURL(): String {
        return this.imageURL
    }

    fun getDescription(): String {
        return this.description
    }

    fun getWordCount(): String {
        return this.wordCount
    }

    fun getCompleted(): String {
        return this.completed
    }
}