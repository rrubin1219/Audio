package edu.temple.audiobb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(_bookList: BookList, _onClick: (Book) -> Unit): RecyclerView.Adapter<BookAdapter.BookViewHolder>(){
    private val bookList = _bookList
    private val onClick = _onClick

    //Assigning the book directly to the onClick function instead of the View
    class BookViewHolder(layout: View, onClick: (Book) -> Unit): RecyclerView.ViewHolder(layout){
        val titleTextView : TextView
        val authorTextView: TextView

        lateinit var book: Book

        init{
            titleTextView = layout.findViewById(R.id.titleView)
            authorTextView = layout.findViewById(R.id.authorView)
            titleTextView.setOnClickListener{
                onClick(book)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder{
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_layout, parent, false), onClick)
    }
    // Bind the book to the holder along with the values for the views
    override fun onBindViewHolder(holder: BookViewHolder, position: Int){
        holder.titleTextView.text = bookList[position]?.title
        holder.authorTextView.text = bookList[position]?.author
        holder.book = bookList[position]!!
    }
    override fun getItemCount(): Int{
        return bookList.size()
    }
}