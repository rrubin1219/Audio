package edu.temple.audiobb

import org.json.JSONObject
import java.io.Serializable

// Simple data class
data class Book(val id: Int, val title: String, val author: String, val coverURL: String, val duration: Int) : Serializable {
    constructor(book: JSONObject) : this(book.getInt("id"), book.getString("title"), book.getString("author"), book.getString("cover_url"), book.getInt("duration"))
}