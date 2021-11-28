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

class ControlFragment : Fragment() {
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var progressBar: SeekBar
    private lateinit var nowPlaying: TextView

    private lateinit var book: Book
    val max = book.duration

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

        nowPlaying.text = book.title

        var spot: Int
        val bundle = Bundle()

        playButton.setOnClickListener{
            bundle.putBoolean("play", true)
            bundle.putBoolean("pause", false)
            bundle.putBoolean("stop", false)
        }
        pauseButton.setOnClickListener{
            bundle.putBoolean("play", false)
            bundle.putBoolean("pause", true)
            bundle.putBoolean("stop", false)
        }
        stopButton.setOnClickListener{
            bundle.putBoolean("play", false)
            bundle.putBoolean("pause", false)
            bundle.putBoolean("stop", true)
        }
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                bundle.putBoolean("fromUser", fromUser)
                bundle.putInt("progress", progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val p = seekBar.progress/100 //Percent of progress
                spot = p*max
                bundle.putInt("spot", spot)
            }
        })
        return layout
    }
}