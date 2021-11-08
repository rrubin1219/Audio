package edu.temple.audiobb

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
    private fun fetchBook(term: String?) {
        val url = "https://kamorris.com/lab/cis3515/search.php?term=$term"
        val intent = Intent(this, MainActivity::class.java)

        volleyQueue.add(
            JsonObjectRequest(Request.Method.GET, url, null, {
                    Log.d("Response", it.toString())
                    try {
                        intent.putExtra("title", it.getString("title"))
                        intent.putExtra("author", it.getString("author"))
                        intent.putExtra("cover", it.getString("cover_url"))
                        intent.putExtra("id", it.getInt("id"))
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }
}