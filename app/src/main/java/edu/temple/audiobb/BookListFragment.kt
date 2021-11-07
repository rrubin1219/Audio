package edu.temple.audiobb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val BOOK_LIST = "bookList"

class BookListFragment: Fragment(){
    private var bookList: BookList? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookList = it.getSerializable(BOOK_LIST) as BookList?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        //Start Dialog Activity
        val dialogButton = view.findViewById<Button>(R.id.dialogButton)
        dialogButton.setOnClickListener{
            val intent = Intent(requireContext(), BookSearchActivity::class.java)
            startActivity(intent)
        }

        val bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
        recyclerView = view.findViewById(R.id.recyclerView)

        val onClick : (Book) -> Unit = {
            // Update the ViewModel
            book: Book -> bookViewModel.setBook(book)
            // Inform the activity of the selection so as to not have the event replayed when the activity is restarted
            (activity as BookSelectedInterface).bookSelected()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = BookListAdapter (bookList!!, onClick)
        }
    }
    companion object {
        fun newInstance(bookList: BookList) = BookListFragment().apply{
            arguments = Bundle().apply{
                putSerializable(BOOK_LIST, bookList)
            }
        }
    }
    interface BookSelectedInterface{
        fun bookSelected()
    }
}