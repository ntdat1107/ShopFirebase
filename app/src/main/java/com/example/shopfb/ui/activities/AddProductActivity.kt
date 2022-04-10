package com.example.shopfb.ui.activities

import android.Manifest
import android.content.Context
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
import com.example.shopfb.databinding.ActivityAddProductBinding
import com.example.shopfb.models.Product
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.FirestoreClass
import com.example.shopfb.utils.GlideLoader
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class AddProductActivity : BaseActivity() {
    private var binding: ActivityAddProductBinding? = null
    private var mProductImageURL: String = ""
    private var mSelectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpActionBar()

        binding?.ivAddUpdateProduct!!.setOnClickListener {
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

        binding?.btnSubmit!!.setOnClickListener {
            if (validateProductDetails()) {
                showProgressDialog("Please wait")
                FirestoreClass().uploadImageToCloudStorage(
                    this,
                    mSelectedImageUri,
                    Constants.PRODUCT_IMAGE
                )
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails() {
        val userName = this.getSharedPreferences(
            Constants.LOGGED_IN_USERNAME,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "")!!
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            userName,
            binding?.etProductTitle!!.text.toString().trim { it <= ' ' },
            binding?.etProductPrice!!.text.toString().trim { it <= ' ' },
            binding?.etProductDescription!!.text.toString().trim { it <= ' ' },
            binding?.etProductQuantity!!.text.toString().trim { it <= ' ' },
            mProductImageURL
        )
        FirestoreClass().uploadProductDetails(this, product)
    }

    fun uploadProductSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Your product is upload successfully", false)
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            finish()
        }, 500)
    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageUri == null -> {
                showErrorSnackBar("Please select product image", true)
                false
            }
            TextUtils.isEmpty(binding?.etProductTitle!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter product title", true)
                false
            }
            TextUtils.isEmpty(
                binding?.etProductDescription!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter product description", true)
                false
            }
            TextUtils.isEmpty(binding?.etProductPrice!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter product price", true)
                false
            }
            TextUtils.isEmpty(binding?.etProductQuantity!!.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter product quantity", true)
                false
            }
            else -> true
        }
    }

    @Suppress("DEPRECATION")
    private fun showImageChooser() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
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
                            binding!!.ivProductImage
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar("Image selection failed", true)
                    }
                }
            }
        }
    }

    private fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(binding?.root!!, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorSnackBarError)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarAddProductActivity)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarAddProductActivity?.setNavigationOnClickListener { onBackPressed() }
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
}