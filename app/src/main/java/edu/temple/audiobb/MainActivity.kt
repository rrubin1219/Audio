package edu.temple.audiobb

import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService


class MainActivity: AppCompatActivity(), BookListFragment.BookSelectedInterface, ControlFragment.MediaControlInterface{
    private val isSingleContainer: Boolean by lazy{
        findViewById<View>(R.id.displayContainer) == null
    }
    private val selectedBookViewModel: BookViewModel by lazy{
        ViewModelProvider(this).get(BookViewModel::class.java)
    }
    private val playingBookViewModel : PlayingBookViewModel by lazy {
        ViewModelProvider(this).get(PlayingBookViewModel::class.java)
    }
    private val bookListViewModel: BookList by lazy {
        ViewModelProvider(this).get((BookList::class.java))
    }
    private val dialogButton: Button by lazy {
        findViewById(R.id.dialogButton)
    }

    var isConnected = false
    lateinit var playerBinder: PlayerService.MediaControlBinder //Service Binder
    private lateinit var bookListFragment: BookListFragment
    private lateinit var service: Intent

    private val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        supportFragmentManager.popBackStack()
        it.data?.run {
            bookListViewModel.copyBooks(getSerializableExtra(BookList.BOOKLIST_KEY) as BookList)
            bookListFragment.listUpdated()
        }
    }

    //Service Handler
    val playerHandler = Handler(Looper.getMainLooper()) { msg ->
        msg.obj?.let { msgObj ->
            val bookProgress = msgObj as PlayerService.BookProgress

            if (playingBookViewModel.getPlayingBook().value == null) {
                Volley.newRequestQueue(this)
                    .add(JsonObjectRequest(Request.Method.GET, API.getBookDataUrl(bookProgress.bookId), null, { jsonObject ->
                        playingBookViewModel.setPlayingBook(Book(jsonObject))
                        if (selectedBookViewModel.getBook().value == null) {
                            selectedBookViewModel.setBook(playingBookViewModel.getPlayingBook().value)
                            bookSelected()
                        }
                    }, {}))
            }
            supportFragmentManager.findFragmentById(R.id.controlContainer)?.run{
                with (this as ControlFragment) {
                    playingBookViewModel.getPlayingBook().value?.also {
                        setPlayProgress(((bookProgress.progress / it.duration.toFloat()) * 100).toInt())
                    }
                }
            }
        }
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

        playingBookViewModel.getPlayingBook().observe(this, {
            (supportFragmentManager.findFragmentById(R.id.controlFragmentContainer) as ControlFragment).setNowPlaying(it.title)
        })

        //Service Intent
        service = Intent(this, PlayerService::class.java)

        //Bind Service Connection
        bindService(service, serviceConnection, BIND_AUTO_CREATE)

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

    override fun play() {
        if (isConnected && selectedBookViewModel.getBook().value != null) {
            Log.d("Button pressed", "Play button")
            playerBinder.play(selectedBookViewModel.getBook().value!!.id)
            playingBookViewModel.setPlayingBook(selectedBookViewModel.getBook().value)
            startService(service)
        }

    }
    override fun pause() {
        if (isConnected) playerBinder.pause()
    }
    override fun stop() {
        if (isConnected) {
            playerBinder.stop()
            stopService(service)
        }
    }
    override fun seek(position: Int) {
        // Converting percentage to proper book progress
        if (isConnected && playerBinder.isPlaying) playerBinder.seekTo((playingBookViewModel.getPlayingBook().value!!.duration * (position.toFloat() / 100)).toInt())
    }

    //Downloading audio file
    fun download(id: Int): Long {
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(API.downloadBook(id))
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        return manager.enqueue(request)
    }
}