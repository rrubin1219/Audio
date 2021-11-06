package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class BookSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        title = "Search"

        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener{

        }
    }
}