package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlin.properties.Delegates

class BookSearchActivity : AppCompatActivity() {
    private val volleyQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }
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
        val intent = Intent(this, MainActivity::class.java)
        volleyQueue.add(
            JsonObjectRequest(Request.Method.GET, url, null, {
                    Log.d("Response", it.toString())
                    try {
                        intent.putExtra("books", Book(it.getString("title"), it.getString("author"),it.getInt("id"), it.getString("cover_url")))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
            )
        )
        setResult(RESULT_OK, intent)
        finish()
    }
}