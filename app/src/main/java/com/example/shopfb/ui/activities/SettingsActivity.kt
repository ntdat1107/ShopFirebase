package com.example.shopfb.ui.activities

import android.content.Intent
import android.os.Bundle
import com.example.shopfb.databinding.ActivitySettingsBinding
import com.example.shopfb.models.User
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.FirestoreClass
import com.example.shopfb.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity() {
    private var binding: ActivitySettingsBinding? = null
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpActionBar()

        binding?.tvEdit?.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        }

        binding?.btnLogout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarSettingsActivity)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarSettingsActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog("Please wait")
        FirestoreClass().getUserDetails(this)
    }

    fun getUserDetailsSuccess(user: User) {
        mUserDetails = user
        hideProgressDialog()
        GlideLoader(this).loadUserPicture(user.image, binding?.ivUserPhoto!!)
        var name = "${user.firstName} ${user.lastName}"
        binding?.tvName!!.text = name
        binding?.tvGender!!.text = user.gender
        binding?.tvEmail!!.text = user.email
        binding?.tvMobileNumber!!.text = "${user.mobile}"
    }
}