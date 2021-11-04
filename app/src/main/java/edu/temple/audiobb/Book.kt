package edu.temple.audiobb

import java.io.Serializable

//Data class
data class Book(val title: String, val author: String, val id: Int, val coverURL: String): Serializable
