package com.example.inputform

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.inputform.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginEditText.listenForChanges {
            binding.textInputLayout.isErrorEnabled = false
        }
        binding.loginButton.setOnClickListener {
            processInputError(binding.loginEditText.text.toString())
        }
    }

    private fun processInputError(input: String) {
        Log.d(TAG, "processInputError called")
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
        binding.textInputLayout.isErrorEnabled = !isValid
        val error = if (isValid) "" else getString(R.string.invalid_email)
        binding.textInputLayout.error = error

        if (isValid) {
            binding.loginButton.isEnabled = false

            hideKeyboard(binding.loginEditText)
            Snackbar.make(
                binding.loginButton,
                getString(R.string.valid_email),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun TextInputEditText.listenForChanges(func: (text: String) -> Unit) {
        addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(str: Editable?) {
                func.invoke(str.toString())
            }
        })
    }

    private fun AppCompatActivity.hideKeyboard(view: View) {
        val inputMethodManager = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}