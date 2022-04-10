package com.example.shopfb.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shopfb.R
import com.example.shopfb.databinding.ActivityLoginBinding
import com.example.shopfb.models.User
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.FirestoreClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {
    private var binding: ActivityLoginBinding? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        checkIsLogin()

        binding?.tvRegister?.setOnClickListener {
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
        }
        binding?.btnLogin?.setOnClickListener {
            val isValid: Boolean = validateLoginDetail()
            if (isValid) {
                login()
            }
        }
        binding?.tvForgotPwd?.setOnClickListener {
            val i = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(i)
        }
    }

    private fun checkIsLogin() {
        showProgressDialog("Wait a second")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirestoreClass().getUserDetails(this@LoginActivity)
        } else {
            hideProgressDialog()
        }
    }

    private fun login() {
        showProgressDialog("Please wait")
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val pwd: String = binding?.etPwd?.text.toString().trim { it <= ' ' }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirestoreClass().getUserDetails(this@LoginActivity)
                } else {
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    private fun validateLoginDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter email", true)
                false
            }
            TextUtils.isEmpty(binding?.etPwd?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter password", true)
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
                ContextCompat.getColor(this@LoginActivity, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@LoginActivity, R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }


    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        Log.i("First name", user.firstName)
        Log.i("Last name", user.lastName)
        Log.i("Email", user.email)

        if (user.profileCompleted == 0) {
            val i = Intent(this@LoginActivity, UserProfileActivity::class.java)
            i.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(i)
            finish()
        } else {
            val i = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}