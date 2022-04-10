package com.example.shopfb.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.shopfb.databinding.ActivityProductDetailsBinding
import com.example.shopfb.models.CartItem
import com.example.shopfb.models.Product
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.FirestoreClass
import com.example.shopfb.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {

    private var binding: ActivityProductDetailsBinding? = null
    private var mProductID: String = ""
    private var mProductUserID: String = ""
    private lateinit var mProductDetails: Product
    private val mUserID: String = FirestoreClass().getCurrentUserID()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater);
        setContentView(binding?.root)

        setToolBar()


        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductUserID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            if (mProductUserID != mUserID) {
//                Log.i("check", "false")
                binding?.btnAddToCart!!.visibility = View.VISIBLE
                binding?.btnGoToCart!!.visibility = View.VISIBLE
            } else {
//                Log.i("check", "true")
                binding?.btnAddToCart!!.visibility = View.GONE
                binding?.btnGoToCart!!.visibility = View.GONE
            }
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product ID:", mProductID)
            showProgressDialog("Please wait")
            FirestoreClass().getProductDetailsByID(this, mProductID)
        }

        binding?.btnAddToCart!!.setOnClickListener {
            addToCart()
        }
        binding?.btnGoToCart!!.setOnClickListener {
            var intent: Intent = Intent(this, CartListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setToolBar() {
        setSupportActionBar(binding?.toolbarProductDetailsActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarProductDetailsActivity!!.setNavigationOnClickListener { onBackPressed() }
    }

    fun onSuccessGetProduct(product: Product) {
        mProductDetails = product
        GlideLoader(this).loadProductPicture(product.image, binding?.ivProductDetailImage!!)
        binding?.tvProductDetailsTitle!!.text = product.title
        binding?.tvProductDetailsPrice!!.text = "$${product.price}"
        binding?.tvProductDetailsDescription!!.text = product.description
        binding?.tvProductQuantity!!.text = product.stock_quantity
        if (product.user_id != mUserID) {
            FirestoreClass().checkIfProductInCart(this, mProductID)
        } else {
            hideProgressDialog()
        }
    }

    private fun addToCart() {
        val cartItem = CartItem(
            mUserID,
            mProductDetails.product_id,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            "1",
            mProductDetails.stock_quantity
        )
        showProgressDialog("Please wait")
        FirestoreClass().addCartItems(this, cartItem)
    }

    fun onAddToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Product was added to your cart successfully", Toast.LENGTH_SHORT)
            .show()
        binding?.btnAddToCart!!.visibility = View.GONE
        binding?.btnGoToCart!!.visibility = View.VISIBLE
    }

    fun productExistInCart() {
        hideProgressDialog()
        binding?.btnAddToCart!!.visibility = View.GONE
        binding?.btnGoToCart!!.visibility = View.VISIBLE
    }
}