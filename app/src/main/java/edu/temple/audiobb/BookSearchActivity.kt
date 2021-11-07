package edu.temple.audiobb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException

class BookSearchActivity : AppCompatActivity() {
    private val volleyQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        title = "Search"

        val search = findViewById<Button>(R.id.searchView)
        val searchText = findViewById<EditText>(R.id.searchTermView)

       // val fragment = supportFragmentManager.beginTransaction()
        search.setOnClickListener {
            fetchBook(searchText.text.toString())
        }
    }
    private fun fetchBook(term: String?) {
        val url = "http://kamorris.com/lab/cis3515/search.php?term=$term"
        val intent = Intent(this, MainActivity::class.java)

        volleyQueue.add(
            JsonObjectRequest(
                Request.Method.GET, url, null, {
                    Log.d("Response", it.toString())
                    try {
                        intent.putExtra("title", it.getString("title"))
                        intent.putExtra("author", it.getString("author"))
                        intent.putExtra("cover", it.getString("cover_url"))
                        intent.putExtra("id", it.getString("id"))
                        //titleView.text = it.getString("title")
                        //authorView.text = it.getString("author")
                        //Picasso.get().load(it.getString("cover_url")).into(coverView)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }

    //fetchBook(mes)

}