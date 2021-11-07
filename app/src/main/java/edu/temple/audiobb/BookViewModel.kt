package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel: ViewModel(){
    private val book: MutableLiveData<Book> by lazy{
        MutableLiveData<Book>()
    }
    fun getBook(): LiveData<Book>{
        return book
    }
    fun setBook(selectedBook: Book?){
        this.book.value = selectedBook
    }
}