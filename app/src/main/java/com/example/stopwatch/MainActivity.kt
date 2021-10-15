package com.example.stopwatch

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.stopwatch.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

// TODO: [BUG] CountDownTimer runs for an extra second

class MainActivity : AppCompatActivity() {

    private inner class MyOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {

            val editText = binding.editTextTime

            when {
                editText.text.isBlank() -> {
                    // Missing minute count
                    Snackbar.make(binding.root, "Enter the minute count", Snackbar.LENGTH_SHORT)
                        .show()
                }

                isReset -> {
                    // Start the timer
                    val minutes = editText.text.toString().toInt()

                    // Handle negative values
                    if (minutes < 0) {
                        Snackbar.make(
                            binding.root,
                            "Only non-negative numbers are allowed",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        return
                    }

                    editText.isEnabled = false
                    countDownTimer = newStopWatchTimer(minutes)
                    binding.circularIndicator.max = minutes
                    countDownTimer.start()
                    isReset = false
                    (v as Button).text = getString(R.string.reset)
                }

                else -> {
                    // Reset the timer
                    editText.isEnabled = true
                    countDownTimer.cancel()
                    updateStopWatchUi(binding.circularIndicator.max)
                    isReset = true
                    (v as Button).text = getString(R.string.start)
                }
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var countDownTimer: CountDownTimer
    private var isReset = true

    private fun newStopWatchTimer(minuteCount: Int): CountDownTimer {
        return object :
            CountDownTimer((minuteCount * 60 * 1000).toLong(), (60 * 1000).toLong()) {

            override fun onTick(millisUntilFinished: Long) {
                updateStopWatchUi(millisUntilFinished.toInt() / (60 * 1000))
            }

            override fun onFinish() {
                Snackbar.make(binding.root, R.string.time_up, Snackbar.LENGTH_SHORT).show()
                binding.editTextTime.isEnabled = true
                binding.editTextTime.setText("")
                binding.btnStartResetTimer.text = getString(R.string.start)
            }
        }
    }

    private fun updateStopWatchUi(progress: Int) {
        binding.circularIndicator.progress = progress
        binding.editTextTime.setText("$progress")
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restrict app to portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Inflate corresponding layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initial state of the stopwatch UI
        val max = binding.circularIndicator.max
        binding.circularIndicator.progress = max

        // Respond to button taps
        binding.btnStartResetTimer.setOnClickListener(MyOnClickListener())
    }

    // Hide keyboard upon tapping elsewhere
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev?.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus is EditText) {
                val view = currentFocus as EditText
                view.clearFocus()

                val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}