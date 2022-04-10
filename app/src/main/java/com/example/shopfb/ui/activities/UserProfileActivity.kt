package com.example.shopfb.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopfb.R
import com.example.shopfb.databinding.ActivityUserProfileBinding
import com.example.shopfb.models.User
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.FirestoreClass
import com.example.shopfb.utils.GlideLoader
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private var binding: ActivityUserProfileBinding? = null
    private var mSelectedImageUri: Uri? = null
    private var mUserProfileImageURL: String = ""
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if (userDetails.profileCompleted == 0) {
            binding?.etFirstName?.isEnabled = false
            binding?.etFirstName!!.setText(userDetails.firstName)
            binding?.etLastName?.isEnabled = false
            binding?.etLastName!!.setText(userDetails.lastName)
            binding?.etEmail?.isEnabled = false
            binding?.etEmail!!.setText(userDetails.email)
        } else {
            setUpActionBar()
            binding?.tvTitle!!.text = "EDIT YOUR PROFILE"
            GlideLoader(this).loadUserPicture(userDetails.image, binding?.ivUserImage!!)
            binding?.etFirstName!!.setText(userDetails.firstName)
            binding?.etLastName!!.setText(userDetails.lastName)
            binding?.etEmail!!.setText(userDetails.email)
            if (userDetails.mobile != 0L) {
                binding?.etNumber!!.setText(userDetails.mobile.toString())
            }
            if (userDetails.gender == Constants.MALE) {
                binding?.rbMale?.isChecked = true
            } else {
                binding?.rbFemale?.isChecked = true
            }
        }

        binding?.btnSubmit!!.setOnClickListener {
            if (validateUserProfileDetail()) {
                showProgressDialog("Please wait")
                if (mSelectedImageUri != null) {
                    Log.i("Image choose URI", mSelectedImageUri.toString())
                    FirestoreClass().uploadImageToCloudStorage(
                        this,
                        mSelectedImageUri,
                        Constants.USER_PROFILE_IMAGE
                    )
                } else {
                    // Log.i("Image choose URI", mSelectedImageUri.toString())
                    updateUserProfile()
                }

            }
        }

        binding?.ivUserImage!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // showErrorSnackBar("Already granted", false)
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_EXTERNAL_PERMISSION_CODE
                )
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarProfileActivity)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarProfileActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updateUserProfile() {
        val userHashMap = HashMap<String, Any>()

        val firstName = binding?.etFirstName?.text.toString().trim { it <= ' ' }
        val lastName = binding?.etLastName?.text.toString().trim { it <= ' ' }
        val email = binding?.etEmail?.text.toString().trim { it <= ' ' }
        if (firstName != userDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        if (lastName != userDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        if (email != userDetails.email) {
            userHashMap[Constants.EMAIL] = email
        }

        val mobileNumber = binding?.etNumber?.text.toString().trim { it <= ' ' }
        val gender = if (binding?.rbMale!!.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != userDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if (mUserProfileImageURL != "") {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if (gender != userDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        if (userDetails.profileCompleted == 0) {
            userHashMap[Constants.PROFILE_COMPLETE] = 1
        }
        FirestoreClass().updateUserProfile(this, userHashMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_EXTERNAL_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // showErrorSnackBar("The storage permission is granted", false)
                showImageChooser()
            } else {
                showErrorSnackBar("The storage permission is denied", true)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageUri = data.data!!
                        Log.i("Image choose", mSelectedImageUri.toString())
                        // binding?.ivUserImage?.setImageURI(mSelectedImageUri)
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageUri!!,
                            binding!!.ivUserImage
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar("Image selection failed", true)
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun showImageChooser() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfile()
    }

    private fun validateUserProfileDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding?.etNumber!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter phone number", true)
                false
            }
            TextUtils.isEmpty(binding?.etFirstName!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter first name", true)
                false
            }
            TextUtils.isEmpty(binding?.etLastName!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter last name", true)
                false
            }
            TextUtils.isEmpty(binding?.etEmail!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter email", true)
                false
            }
            else -> true
        }
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(binding?.root!!, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@UserProfileActivity, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@UserProfileActivity, R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Your profile is updated successfully", false)
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
            finish()
        }, 500)
    }
}