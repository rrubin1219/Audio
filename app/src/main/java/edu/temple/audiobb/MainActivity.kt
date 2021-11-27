package edu.temple.audiobb

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService
import kotlin.properties.Delegates


class MainActivity: AppCompatActivity(), BookListFragment.BookSelectedInterface{
    private val isSingleContainer: Boolean by lazy{
        findViewById<View>(R.id.displayContainer) == null
    }
    private val selectedBookViewModel: BookViewModel by lazy{
        ViewModelProvider(this).get(BookViewModel::class.java)
    }
    private val bookListViewModel: BookList by lazy {
        ViewModelProvider(this).get((BookList::class.java))
    }
    private val dialogButton: Button by lazy {
        findViewById(R.id.dialogButton)
    }

    var isConnected = false
    lateinit var playerBinder: PlayerService.MediaControlBinder //Service Binder
    var bookProgress by Delegates.notNull<Int>()
    private lateinit var bookListFragment: BookListFragment

    private val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        supportFragmentManager.popBackStack()
        it.data?.run {
            bookListViewModel.copyBooks(getSerializableExtra(BookList.BOOKLIST_KEY) as BookList)
            bookListFragment.listUpdated()
        }
    }

    //Service Handler
    private val playerHandler = Handler(Looper.getMainLooper()){
        bookProgress = (it.obj as PlayerService.BookProgress).progress
        true
    }

    //Service Connection
    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            playerBinder = service as PlayerService.MediaControlBinder
            playerBinder.setProgressHandler(playerHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }

    companion object{
        const val BOOKLIST_KEY = "BookList"
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Start Dialog Activity
        dialogButton.setOnClickListener{
            result.launch(Intent(this, BookSearchActivity::class.java))
        }

        val play = false
        val pause = false
        val stop = false

        if(play == true && isConnected){
            playerBinder.play(0)
        }
        if(pause == true && isConnected){
            playerBinder.pause()
        }
        if(stop == true && isConnected){
            playerBinder.stop()
        }




        //Bind Service Connection
        bindService(Intent(this, PlayerService::class.java), serviceConnection, BIND_AUTO_CREATE)

        //Switching from one container to two containers clear BookDisplayFragment from listContainer
        if (supportFragmentManager.findFragmentById(R.id.listContainer) is BookDisplayFragment && selectedBookViewModel.getBook().value != null) {
            supportFragmentManager.popBackStack()
        }
        //First time the activity is loading, add a BookListFragment
        if (savedInstanceState == null) {
            bookListFragment = BookListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.listContainer, bookListFragment, BOOKLIST_KEY)
                .commit()
        }
        else {
            bookListFragment = supportFragmentManager.findFragmentByTag(BOOKLIST_KEY) as BookListFragment
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

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

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