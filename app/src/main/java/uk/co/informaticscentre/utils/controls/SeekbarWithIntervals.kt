package uk.co.informaticscentre.utils.controls

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import kotlinx.android.synthetic.main.seekbar_with_intervals.view.*
import java.util.concurrent.atomic.AtomicInteger

import uk.co.informaticscentre.utils.R

class SeekbarWithIntervals(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var Seekbar: SeekBar? = null

    private var WidthMeasureSpec = 0
    private var HeightMeasureSpec = 0
    private var isAlignmentResetOnLayoutChange: Boolean = false

    private val activity: Activity
        get() = context as Activity

    private val seekbarThumbWidth: Int
        get() = resources.getDimensionPixelOffset(R.dimen.seekbar_thumb_width)

    var progress: Int
        get() = seekbar!!.progress
        set(progress) {
            seekbar!!.progress = progress
        }

    private val seekbar: SeekBar?
        get() {
            if (Seekbar == null) {
                Seekbar = findViewById<View>(R.id.seekbar) as SeekBar
            }

            return Seekbar
        }


    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.seekbar_with_intervals, this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (changed) {
            if (!isAlignmentResetOnLayoutChange) {
               alignIntervals()

                // We've changed the intervals layout, we need to refresh.
                intervalsLayout.measure(WidthMeasureSpec, HeightMeasureSpec)
                intervalsLayout.layout(intervalsLayout.left, intervalsLayout.top, intervalsLayout.right, intervalsLayout.bottom)
            }
        }
    }


    private fun alignIntervals() {

        if (seekbar != null) {
            val widthOfSeekbarThumb = seekbarThumbWidth
            val thumbOffset = widthOfSeekbarThumb / 2

            val widthOfSeekbar = seekbar!!.width
            val firstIntervalWidth = intervalsLayout.getChildAt(0).width
            val remainingPaddableWidth = widthOfSeekbar - firstIntervalWidth - widthOfSeekbarThumb

            val numberOfIntervals = seekbar!!.max
            val maximumWidthOfEachInterval = remainingPaddableWidth / numberOfIntervals

            alignFirstInterval(thumbOffset, maximumWidthOfEachInterval)
            alignIntervalsInBetween(maximumWidthOfEachInterval)
            alignLastInterval(thumbOffset, maximumWidthOfEachInterval)
            isAlignmentResetOnLayoutChange = true
        }
    }

    private fun alignFirstInterval(offset: Int, maximumWidthOfEachInterval: Int) {
        val firstInterval = intervalsLayout.getChildAt(0) as TextView
        val widthOfText = firstInterval.width
        val rightPadding = Math.round((maximumWidthOfEachInterval -widthOfText - offset).toFloat()/2)
        firstInterval.setPadding(offset, 0, rightPadding, 0)
    }

    private fun alignIntervalsInBetween(maximumWidthOfEachInterval: Int) {
        var widthOfPreviousIntervalsText = 0

        // Don't align the first or last interval.
        for (index in 1 until intervalsLayout.childCount - 1) {
            val textViewInterval = intervalsLayout.getChildAt(index) as TextView
            val widthOfText = textViewInterval.width

            // This works out how much left padding is needed to center the current interval.
            val horizontalPadding = Math.round((maximumWidthOfEachInterval - widthOfText / 2 - widthOfPreviousIntervalsText / 2).toFloat()/2)
            textViewInterval.setPadding(horizontalPadding, 0, horizontalPadding, 0)

            widthOfPreviousIntervalsText = widthOfText
        }
    }

    private fun alignLastInterval(offset: Int, maximumWidthOfEachInterval: Int) {
        val lastIndex = intervalsLayout.childCount - 1

        val lastInterval = intervalsLayout.getChildAt(lastIndex) as TextView
        val widthOfText = lastInterval.width

        val leftPadding = Math.round((maximumWidthOfEachInterval - widthOfText - offset).toFloat()/2)
        lastInterval.setPadding(leftPadding, 0, 0, 0)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        WidthMeasureSpec = widthMeasureSpec
        HeightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setIntervals(intervals: List<String>) {
        displayIntervals(intervals)
        seekbar!!.max = intervals.size - 1
    }

    private fun displayIntervals(intervals: List<String>) {
        if (intervalsLayout.childCount == 0) {
            for (interval in intervals) {
                val textViewInterval = createInterval(interval)
                intervalsLayout.addView(textViewInterval)
            }
        }
    }

    private fun createInterval(interval: String): TextView {
        val textBoxView = LayoutInflater.from(context)
                .inflate(R.layout.seekbar_with_intervals_labels, null) as View

        val textView = textBoxView
                .findViewById<View>(R.id.textViewInterval) as TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            textView.id = View.generateViewId()
        else
            textBoxView.id = generateViewId()

        textView.text = interval

        return textView
    }

    fun setAlignmentResetOnLayoutChange() {
        alignIntervals()

        // We've changed the intervals layout, we need to refresh.
        intervalsLayout.measure(WidthMeasureSpec, HeightMeasureSpec)
        intervalsLayout.layout(intervalsLayout.left, intervalsLayout.top, intervalsLayout.right, intervalsLayout.bottom)

    }


    fun setOnSeekBarChangeListener(onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener) {

        seekbar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                for (i in 0 until intervalsLayout.childCount) {
                    val tv = intervalsLayout.getChildAt(i) as TextView
                    if (i == seekBar.progress)
                        tv.setTextColor(resources.getColor(R.color.colorPrimary))
                    else
                        tv.setTextColor(resources.getColor(R.color.white))
                }
                onSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                onSeekBarChangeListener.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onSeekBarChangeListener.onStopTrackingTouch(seekBar)
            }
        })


    }


    companion object {

        fun dpToPx(context: Context, valueInDp: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
        }

        private val sNextGeneratedId = AtomicInteger(1)

        /**
         * Generate a value suitable for use in [.setId].
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */
        fun generateViewId(): Int {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }
    }
}