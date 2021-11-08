package edu.temple.audiobb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException

class BookDisplayFragment: Fragment(){
    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var coverView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val layout = inflater.inflate(R.layout.fragment_book_display, container, false)

        titleView = layout.findViewById(R.id.titleView)
        authorView = layout.findViewById(R.id.authorView)
        coverView = layout.findViewById(R.id.coverView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        ViewModelProvider(requireActivity()).get(BookViewModel::class.java).getBook().observe(requireActivity(),{updateBook(it)})
    }

    private fun updateBook(book: Book?){
        book?.run{
            titleView.text = title
            authorView.text = author
            if (id != null) {
                coverView.setImageResource(id)
            }
        }
    }
}