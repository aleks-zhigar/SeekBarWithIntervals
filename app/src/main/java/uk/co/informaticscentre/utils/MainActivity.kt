package uk.co.informaticscentre.utils

import android.os.Bundle
import android.view.View

import android.widget.SeekBar
import android.widget.Toast

import java.util.ArrayList

import androidx.appcompat.app.AppCompatActivity
import uk.co.informaticscentre.utils.controls.SeekbarWithIntervals


class MainActivity : AppCompatActivity() {
    private var SeekbarWithIntervals: SeekbarWithIntervals? = null

    private val intervals: List<String>
        get() = object : ArrayList<String>() {
            init {
                add("Auto")
                add("5")
                add("15")
                add("50")
                add("100")
                add("300")
                add("500")
            }
        }

    private val seekbarWithIntervals: SeekbarWithIntervals?
        get() {
            if (SeekbarWithIntervals == null) {
                SeekbarWithIntervals = findViewById<View>(R.id.seekbarWithIntervals) as SeekbarWithIntervals?
            }

            return SeekbarWithIntervals
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seekbarIntervals = intervals
        seekbarWithIntervals!!.setIntervals(seekbarIntervals)

        seekbarWithIntervals!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(this@MainActivity, "onStopTrackingTouch", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
