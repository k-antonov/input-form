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
const val KEY_STATE = "State"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private companion object {
        const val INITIAL = "Initial"
        const val INPUT_ERROR = "Input Error"
        const val PROGRESS = "Progress"
        const val SUCCESS = "Success"
        const val FAILED = "Failed"
    }

    private var state = INITIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            state = savedInstanceState.getString(KEY_STATE, state)
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_STATE, state)
    }

    private val textWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(str: Editable?) {
            binding.loginInputLayout.isErrorEnabled = false
            binding.passwordInputLayout.isErrorEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        binding.loginEditText.removeTextChangedListener(textWatcher)
        binding.passwordEditText.removeTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume state: $state")
        binding.loginEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        when (state) {
            INITIAL -> { switchViewsVisibility(true) }
            FAILED -> { showDialog() }
            INPUT_ERROR -> processInputErrors(
                binding.loginEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
            PROGRESS -> {
                switchViewsVisibility(false)
                state = FAILED
            }
        }
    }

    private fun processInputErrors(login: String, password: String) : Boolean {
        val isLoginValid = android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()
        handleTextInputLayoutError(binding.loginInputLayout, isLoginValid, getString(R.string.invalid_email))

        val isPasswordValid = Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()
        handleTextInputLayoutError(binding.passwordInputLayout, isPasswordValid, getString(R.string.invalid_password))

        val isCorrect = isLoginValid && isPasswordValid
        if (!isCorrect) state = INPUT_ERROR
        return isCorrect
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
        switchViewsVisibility(false)
        state = PROGRESS
        hideKeyboard(binding.loginEditText)

        Handler(Looper.myLooper()!!).postDelayed({
            showDialog()
            switchViewsVisibility(true)
        }, 3000)
    }

    private fun switchViewsVisibility(flag: Boolean) {
        binding.loginInputLayout.isEnabled = flag
        binding.passwordInputLayout.isEnabled = flag
        binding.checkbox.isEnabled = flag
        binding.loginButton.isEnabled = flag
        binding.progressBar.visibility = if (flag) View.INVISIBLE else View.VISIBLE
    }

    private fun showDialog() = AlertDialog.Builder(this)
        .setOnCancelListener { state = INITIAL }
        .setMessage(R.string.service_unavailable).show()
        .also { state = FAILED }

    private fun AppCompatActivity.hideKeyboard(view: View) {
        val inputMethodManager = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}