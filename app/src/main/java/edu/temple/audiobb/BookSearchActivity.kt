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
import com.android.volley.toolbox.JsonArrayRequest
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
    private var list: BookList? = null

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
        val bundle = Bundle()

        volleyQueue.add(
            JsonArrayRequest(Request.Method.GET, url, null, {
                    result -> try {
                            for(i in 0 until result.length()){
                                val book = result.getJSONObject(i)
                                val b = Book(book.getString("title"), book.getString("author"), book.getInt("id"),  book.getString("cover_url"))
                                Log.d("list", b.toString())
                                list?.add(b)
                            }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
            )
        )

        //Sending info over
        bundle.putSerializable("list", list)
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }
}