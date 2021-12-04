package edu.temple.audiobb

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import java.io.File

class ControlFragment : Fragment() {
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private var progressBar: SeekBar? = null
    private var nowPlaying: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_control, container, false)

        playButton = layout.findViewById(R.id.playButton)
        pauseButton = layout.findViewById(R.id.pauseButton)
        stopButton = layout.findViewById(R.id.stopButton)
        progressBar = layout.findViewById(R.id.seekBar)
        nowPlaying = layout.findViewById(R.id.nowPlaying)

        playButton.setBackgroundColor(Color.GREEN)
        pauseButton.setBackgroundColor(Color.YELLOW)
        stopButton.setBackgroundColor(Color.RED)

        progressBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2) (activity as MediaControlInterface).seek(p1)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        val onClickListener = View.OnClickListener {
            val parent = activity as MediaControlInterface
            when (it.id) {
                R.id.playButton -> parent.play()
                R.id.pauseButton -> parent.pause()
                R.id.stopButton -> parent.stop()
            }
        }

        playButton.setOnClickListener(onClickListener)
        pauseButton.setOnClickListener(onClickListener)
        stopButton.setOnClickListener(onClickListener)

        return layout
    }

    fun setNowPlaying(title: String) {
        nowPlaying?.text = title
    }
    fun setPlayProgress(progress: Int) {
        progressBar?.setProgress(progress, true)
    }

    interface MediaControlInterface {
        fun play()
        fun pause()
        fun stop()
        fun seek(position: Int)
    }
}