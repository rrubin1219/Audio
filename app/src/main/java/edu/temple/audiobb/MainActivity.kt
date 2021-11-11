package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

class MainActivity: AppCompatActivity(), BookListFragment.BookSelectedInterface{
    private val isSingleContainer: Boolean by lazy{
        findViewById<View>(R.id.displayContainer) == null
    }
    private val selectedBookViewModel: BookViewModel by lazy{
        ViewModelProvider(this).get(BookViewModel::class.java)
    }
    private val dialogButton: Button by lazy {
        findViewById(R.id.dialogButton)
    }
    private var list: BookList? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result != null && result.resultCode == Activity.RESULT_OK){
                list = result.data?.getSerializableExtra("list") as? BookList
                Toast.makeText(this, "Passed", Toast.LENGTH_SHORT).show()
            }
        }

        //Start Dialog Activity
        dialogButton.setOnClickListener{
            val intent = Intent(this, BookSearchActivity::class.java)
            result.launch(intent)
        }

        //Get test data
        val bookList = list//getBookList()//

        //Switching from one container to two containers clear BookDisplayFragment from listContainer
        if (supportFragmentManager.findFragmentById(R.id.listContainer) is BookDisplayFragment) {
            supportFragmentManager.popBackStack()
        }
        //First time the activity is loading, add a BookListFragment
        if (savedInstanceState == null && bookList != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.listContainer, BookListFragment.newInstance(bookList))
                .commit()
        }
        else {
            //Activity loaded previously, If a single container and a selected book, place it on top
                if (isSingleContainer && selectedBookViewModel.getBook().value != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.listContainer, BookDisplayFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
                }
        }
        //Two containers but no BookDetailsFragment, add one to displayContainer
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.displayContainer) !is BookDisplayFragment) {
            supportFragmentManager.beginTransaction()
                .add(R.id.displayContainer, BookDisplayFragment())
                .commit()
        }

        //One container and not displaying BookListFragment
        if(isSingleContainer && supportFragmentManager.findFragmentById(R.id.listContainer) is BookDisplayFragment){
            supportFragmentManager.beginTransaction()
                .replace(R.id.displayContainer, BookListFragment())
                .commit()
        }
    }
/*
    private fun getBookList(): BookList{
        val bookList = BookList()


        //bookList.add(Book("Book 1", "Author", 1,"https:\\/\\/kamorris.com\\/lab\\/abp\\/covers\\/IslandOfDrMoreau.jpeg"))

        bookList.add(Book("Book 0", "Author 9"))
        bookList.add(Book("Book 1", "Author 8"))
        bookList.add(Book("Book 2", "Author 7"))
        bookList.add(Book("Book 3", "Author 6"))
        bookList.add(Book("Book 4", "Author 5"))
        bookList.add(Book("Book 5", "Author 4"))
        bookList.add(Book("Book 6", "Author 3"))
        bookList.add(Book("Book 7", "Author 3"))
        bookList.add(Book("Book 8", "Author 2"))
        bookList.add(Book("Book 9", "Author 0"))



        return bookList
    }
*/
    override fun onBackPressed(){
        //Back press clears the selected book
        selectedBookViewModel.setBook(null)
        super.onBackPressed()
    }

    override fun bookSelected(){
        // Perform a fragment replacement if we only have a single container when a book is selected
        if(isSingleContainer){
            supportFragmentManager.beginTransaction()
                .replace(R.id.listContainer, BookDisplayFragment())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }
}