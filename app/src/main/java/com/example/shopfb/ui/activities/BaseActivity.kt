package com.example.shopfb.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.widget.Toast
import com.example.shopfb.databinding.DialogProgressBinding

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit!!", Toast.LENGTH_SHORT).show()
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            this.doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        val progressBinding: DialogProgressBinding =
            DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(progressBinding.root)
        progressBinding.tvProgressText.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}