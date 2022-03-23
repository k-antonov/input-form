package com.example.inputform

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.inputform.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=." +
        "*[\\\\\\/%§\"&“|`´}{°><:.;#')(@_\$\"!?*=^-]).{8,}\$"
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenForChanges(binding.loginEditText, binding.loginInputLayout)
        listenForChanges(binding.passwordEditText, binding.passwordInputLayout)

        binding.loginButton.setOnClickListener {
            val isInputValid = processInputErrors(
                binding.loginEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
            if (isInputValid) {
                onLoggedIn()
            }
        }

        binding.checkbox.text = SpannableString(getString(R.string.agreement))
        binding.loginButton.isEnabled = false
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            binding.loginButton.isEnabled = isChecked
        }
    }

    private fun listenForChanges(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        editText.listenForChanges { textInputLayout.isErrorEnabled = false }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        processInputErrors(
            binding.loginEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun processInputErrors(login: String, password: String) : Boolean {
        Log.d(TAG, "processInputErrors called")

        val isLoginValid = android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()
        handleTextInputLayoutError(binding.loginInputLayout, isLoginValid, getString(R.string.invalid_email))

        val isPasswordValid = Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()
        handleTextInputLayoutError(binding.passwordInputLayout, isPasswordValid, getString(R.string.invalid_password))

        return isLoginValid && isPasswordValid
    }

    private fun handleTextInputLayoutError(
        textInputLayout: TextInputLayout,
        isValid: Boolean,
        errorText: String
    ) {
        textInputLayout.isErrorEnabled = !isValid
        textInputLayout.error = if (isValid) "" else errorText
    }

    private fun onLoggedIn() {
        fun switchViewsVisibility() {
            binding.loginInputLayout.isEnabled = !binding.loginInputLayout.isEnabled
            binding.passwordInputLayout.isEnabled = !binding.passwordInputLayout.isEnabled
            binding.checkbox.isEnabled = !binding.checkbox.isEnabled
            binding.loginButton.isEnabled = !binding.loginButton.isEnabled
            binding.progressBar.visibility = if (binding.progressBar.visibility == View.VISIBLE)
                View.INVISIBLE else View.VISIBLE
        }

        switchViewsVisibility()
        hideKeyboard(binding.loginEditText)

        Handler(Looper.myLooper()!!).postDelayed({
            AlertDialog.Builder(this).setMessage(R.string.service_unavailable).show()
            switchViewsVisibility()
        }, 3000)
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