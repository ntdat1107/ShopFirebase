package com.example.shopfb.ui.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shopfb.R
import com.example.shopfb.databinding.ActivityRegisterBinding
import com.example.shopfb.models.User
import com.example.shopfb.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {
    private var binding: ActivityRegisterBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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

        binding?.tvLogin?.setOnClickListener {
            onBackPressed()
        }

        setSupportActionBar(binding?.toolbarRegisterActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.btnRegister?.setOnClickListener {
            val isValid: Boolean = validateRegisterDetail()
            if (isValid) {
                registerUser()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun registerUser() {
        showProgressDialog("Please wait")
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val pwd: String = binding?.etPwd?.text.toString().trim { it <= ' ' }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
            OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    val user = User(
                        firebaseUser.uid,
                        binding?.etFirstName?.text.toString().trim { it <= ' ' },
                        binding?.etLastName?.text.toString().trim { it <= ' ' },
                        binding?.etEmail?.text.toString().trim { it <= ' ' }
                    )
                    val db = Firebase.firestore
                    db.collection(Constants.USERS)
                        .document(user.id)
                        .set(user, SetOptions.merge())
                        .addOnSuccessListener {
                            userRegistrationSuccess()
                        }
                        .addOnFailureListener {
                            hideProgressDialog()
                            showErrorSnackBar(it.toString(), true)
                        }

                } else {
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
        )
    }

    private fun userRegistrationSuccess() {
        hideProgressDialog()
        showErrorSnackBar(
            "Register successful",
            false
        )
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            FirebaseAuth.getInstance().signOut()
            finish()
        }, 1000)
    }

    private fun validateRegisterDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etFirstName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter first name!", true)
                false
            }
            TextUtils.isEmpty(binding?.etLastName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter last name!", true)
                false
            }
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter email!", true)
                false
            }
            TextUtils.isEmpty(binding?.etPwd?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter password!", true)
                false
            }
            TextUtils.isEmpty(binding?.etPwdConfirm?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Enter confirm password!", true)
                false
            }
            binding?.etPwd?.text.toString()
                .trim { it <= ' ' } != binding?.etPwdConfirm?.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar("Password and confirm password not same", true)
                false
            }
            binding?.cbTermCondition?.isChecked != true -> {
                showErrorSnackBar("Agree with term and condition", true)
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
                ContextCompat.getColor(this@RegisterActivity, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@RegisterActivity, R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }

}