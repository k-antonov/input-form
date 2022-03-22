package com.example.inputform

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inputform.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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

        // todo somehow get rid of code duplication
        binding.loginEditText.listenForChanges {
            binding.loginInputLayout.isErrorEnabled = false
        }
        binding.passwordEditText.listenForChanges {
            binding.passwordInputLayout.isErrorEnabled = false
        }
        binding.loginButton.setOnClickListener {
            processInputErrors(
                binding.loginEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

    }

    private fun processInputErrors(login: String, password: String) {
        Log.d(TAG, "processInputErrors called")

        // How to move duplicate code to separate function
        // if it is impossible to access binding.param,
        // where param: TextInputLayout?
        val isLoginValid = android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()
        binding.loginInputLayout.isErrorEnabled = !isLoginValid
        val loginError = if (isLoginValid) "" else getString(R.string.invalid_email)
        binding.loginInputLayout.error = loginError

        val isPasswordValid = Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()
        binding.passwordInputLayout.isErrorEnabled = !isPasswordValid
        val passwordError = if (isPasswordValid) "" else getString(R.string.invalid_password)
        binding.passwordInputLayout.error = passwordError

        if (isLoginValid && isPasswordValid) {
            binding.loginButton.isEnabled = false

            hideKeyboard(binding.loginEditText)
            Snackbar.make(
                binding.loginButton,
                getString(R.string.logged_in),
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