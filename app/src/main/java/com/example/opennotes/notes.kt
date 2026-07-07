package com.example.opennotes

// Data model representing a single note
data class Note(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = ""
) {
    // Helper function to convert Firestore map back to Note object
    constructor(map: Map<String, Any>, id: String) : this(
        id = id,
        userId = map["userId"] as? String ?: "",
        title = map["title"] as? String ?: "",
        content = map["content"] as? String ?: ""
    )

    // Helper function to convert Note object to Firestore map format
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "title" to title,
            "content" to content
        )
    }
}