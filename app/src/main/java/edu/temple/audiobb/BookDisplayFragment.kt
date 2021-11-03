package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class BookDisplayFragment: Fragment(){
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val layout = inflater.inflate(R.layout.fragment_book_display, container, false)

        titleTextView = layout.findViewById(R.id.titleView)
        authorTextView = layout.findViewById(R.id.authorView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        ViewModelProvider(requireActivity()).get(BookViewModel::class.java).getBook().observe(requireActivity(), {updateBook(it)})
    }

    private fun updateBook(book: Book?){
        book?.run{
            titleTextView.text = title
            authorTextView.text = author
        }
    }
}