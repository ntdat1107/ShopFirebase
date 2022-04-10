package com.example.shopfb.ui.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shopfb.R
import com.example.shopfb.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private var binding: ActivityForgotPasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setSupportActionBar(binding?.toolbarForgotPasswordActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.btnSubmit?.setOnClickListener {
            val isValid: Boolean = validateEmail()
            if (isValid) {
                resetPwd()
            }
        }
    }

    private fun resetPwd() {
        showProgressDialog("Please wait")
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
            hideProgressDialog()
            if (task.isSuccessful) {
                showErrorSnackBar("Email sent successfully", false)
                @Suppress("DEPRECATION")
                Handler().postDelayed({
                    finish()
                }, 2000)
            } else {
                showErrorSnackBar(task.exception!!.toString(), true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun validateEmail(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter email", true)
                false
            }
            else -> true
        }
    }

    private fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(binding?.root!!, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@ForgotPasswordActivity, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@ForgotPasswordActivity, R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }

}