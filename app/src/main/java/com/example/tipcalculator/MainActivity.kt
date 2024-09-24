package com.example.tipcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.example.tipcalculator.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBarTip.progress = INITIAL_TIP_PERCENT
        binding.textPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        binding.seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                binding.textPercent.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.textNumberDecimal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
        private fun computeTipAndTotal() {

            if (binding.textNumberDecimal.text.isEmpty()) {
                binding.textTipAmount.text = ""
                binding.textTotalAmount.text = ""
                return
            }
            val baseAmount = binding.textNumberDecimal.text.toString().toDouble()
            val tipPercent = binding.seekBarTip.progress
            val tipAmount = baseAmount * tipPercent / 100
            val totalAmount = baseAmount + tipAmount
            binding.textTipAmount.text = "%.2f".format(tipAmount)
            binding.textTotalAmount.text = "%.2f".format(totalAmount)
        }
        @SuppressLint("RestrictedApi")
        private fun updateTipDescription(tipPercent: Int) {
            val tipDescription = when (tipPercent){
                in 0..5 -> "Poor"
                in 6..12 -> "Acceptable"
                in 13..18 -> "Good"
                in 19..24 -> "Great"
                else -> "Amazing"
            }
            binding.textDescription.text = tipDescription
            val color = ArgbEvaluator().evaluate(
                tipPercent.toFloat() / binding.seekBarTip.max,
                ContextCompat.getColor(this, R.color.worst),
                ContextCompat.getColor(this, R.color.best)
            )as Int
            binding.textDescription.setTextColor(color)
        }
}


