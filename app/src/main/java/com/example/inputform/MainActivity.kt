package com.example.inputform

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inputform.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginEditText.addTextChangedListener(textWatcher)
    }

    private val textWatcher: TextWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(str: Editable?) {
            Log.d(TAG, "afterTextChanged called with $str")
            var input = str.toString()

            // todo fix bug: unable to delete text after "@g"
            if (input.endsWith("@g")) {
                Log.d("SimpleTextWatcher", "before setTextCorrectly called")
                input = "${input}mail.com"
                setText(input)
            }

            processInputError(input)
        }
    }

    private fun processInputError(input: String) {
        Log.d(TAG, "processInputError called")
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
        binding.textInputLayout.isErrorEnabled = !isValid
        val error = if (isValid) "" else getString(R.string.invalid_email)
        binding.textInputLayout.error = error

        if (isValid) {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.valid_email),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setText(text: String) {
        binding.loginEditText.removeTextChangedListener(textWatcher)
        binding.loginEditText.setTextCorrectly(text)
        binding.loginEditText.addTextChangedListener(textWatcher)
    }

    private fun TextInputEditText.setTextCorrectly(text: CharSequence) {
        setText(text)
        setSelection(text.length)
    }
}