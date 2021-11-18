package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class BookSearchActivity : AppCompatActivity() {
    private val searchText: TextView by lazy {
        findViewById(R.id.searchTermView)
    }
    private val searchButton: Button by lazy {
        findViewById(R.id.searchButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        title = "Search"

        searchButton.setOnClickListener {
            fetchBook(searchText.text.toString())
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun fetchBook(term: String?) {
        val url = "https://kamorris.com/lab/cis3515/search.php?term=$term"

        Volley.newRequestQueue(this).add(
            JsonArrayRequest(Request.Method.GET, url, null, {
                try {
                    setResult(RESULT_OK, Intent().putExtra(BookList.BOOKLIST_KEY, BookList().apply {populateBooks(it)}))
                    finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
                { Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show() }
            )
        )
    }
}