package com.example.stopwatch

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.stopwatch.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private inner class MyOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {

            val editText = binding.editTextTime

            when {

                // Missing minute count
                editText.text.isBlank() ->

                    Snackbar.make(
                        binding.root,
                        "Enter the minute count",
                        Snackbar.LENGTH_SHORT
                    ).show()

                // Start the timer
                isReset -> {
                    val minutes = editText.text.toString().toInt()

                    if (minutes > 0)
                        startStopwatch(minutes)

                    // Handle non-positive values
                    else
                        Snackbar.make(
                            binding.root,
                            "Only positive numbers are allowed",
                            Snackbar.LENGTH_SHORT
                        ).show()
                }

                // Reset the timer
                else -> resetStopwatch()
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
                val progress = (millisUntilFinished.toInt() / (60 * 1000)) + 1
                updateStopwatchUi(progress)
            }

            override fun onFinish() {
                Snackbar.make(binding.root, R.string.time_up, Snackbar.LENGTH_SHORT).show()
                resetStopwatch()
            }
        }
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
        binding.circularIndicator.progress = binding.circularIndicator.max

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

    fun resetStopwatch() {
        countDownTimer.cancel()
        binding.circularIndicator.progress = binding.circularIndicator.max
        binding.editTextTime.setText("")
        binding.editTextTime.isEnabled = true
        binding.btnStartResetTimer.text = getString(R.string.start)
        isReset = true
    }

    fun startStopwatch(minutes: Int) {
        binding.circularIndicator.max = minutes
        binding.circularIndicator.progress = minutes
        binding.editTextTime.isEnabled = false
        binding.btnStartResetTimer.text = getString(R.string.reset)
        isReset = false
        countDownTimer = newStopWatchTimer(minutes)
        countDownTimer.start()
    }

    fun updateStopwatchUi(progress : Int) {
        binding.circularIndicator.progress = progress
        binding.editTextTime.setText("$progress")
    }
}