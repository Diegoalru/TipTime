package com.darssolutions.tiptime

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.darssolutions.tiptime.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.costOfServiceEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }

        binding.calculateButton.setOnClickListener { calculateTip() }
    }

    /**
     * Calculate the tip based on the user input.
     * Display the tip amount on screen.
     */
    private fun calculateTip() {
        try {
            val stringInTextFiled = binding.costOfServiceEditText.text.toString()
            val cost = stringInTextFiled.toDoubleOrNull()

            // Check if the cost is empty or 0
            if (cost == null) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_empty_cost),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }

            if (cost == 0.0) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_0_as_tip_value),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }

            if (cost < 0.0) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_negative_cost),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }

            // Check which radio button is selected
            val tipPercentage = when (binding.tipOptions.checkedRadioButtonId) {
                R.id.option_twenty_percent -> 0.20
                R.id.option_eighteen_percent -> 0.18
                else -> 0.15
            }

            // Check if the user wants to round up the tip
            val roundUp = binding.roundUpSwitch.isChecked

            // Calculate the tip
            val tip = if (roundUp) {
                kotlin.math.ceil(cost * tipPercentage)
            } else {
                cost * tipPercentage
            }

            // Display the formatted tip value on screen
            displayTip(tip)

        } catch (e: Exception) {
            Snackbar.make(
                binding.root,
                getString(R.string.error_something_went_wrong),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Format the tip amount according to the local currency and display it on screen.
     * @param tip The tip amount to display.
     */
    private fun displayTip(tip: Double) {
        try {
            val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
            binding.tipResult.text = getString(R.string.tip_amount_with_value, formattedTip)
        } catch (e: NumberFormatException) {
            Snackbar.make(
                binding.root,
                getString(R.string.error_number_format),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Hide the keyboard when the user presses the Enter key.
     * @param view The view that received the key event.
     * @param keyCode The value in event.getKeyCode().
     * @return true if the listener has consumed the event, false otherwise.
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}
