package com.example.writableapp.Model

class Project(
    private val pid: String,
    private val uid: String,
    private var title: String,
    private val imageURL: String,
    private var description: String,
    private var wordCount: String,
    private var completed: String
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

    fun setTitle(new_title: String) {
        this.title = new_title
    }

    fun setDescription(new_description: String) {
        this.description = new_description
    }

    fun setWordCount(new_wordCount: String) {
        this.wordCount = new_wordCount
    }

    fun setComplete(new_complete: String) {
        this.completed = new_complete
    }
}