package com.example.shopfb.ui.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopfb.databinding.ActivityCartListBinding
import com.example.shopfb.models.CartItem
import com.example.shopfb.models.Product
import com.example.shopfb.ui.adapters.CartListAdapter
import com.example.shopfb.utils.FirestoreClass

class CartListActivity : BaseActivity() {
    private var binding: ActivityCartListBinding? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartList: ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpActionBar()


    }

    override fun onResume() {
        super.onResume()
        getCartList()
        getAllProduct()
    }

    private fun getAllProduct() {
        showProgressDialog("Please wait")
        FirestoreClass().getAllProducts(this)
    }

    private fun getCartList() {
        showProgressDialog("Please wait")
        FirestoreClass().getCartList(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarCartListActivity)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarCartListActivity!!.setNavigationOnClickListener { onBackPressed() }
    }

    fun onSuccessGetCartList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartList = cartList

        if (mCartList.size > 0) {
            binding?.rvCartItemsList!!.visibility = View.VISIBLE
            binding?.tvNoCartItemFound!!.visibility = View.GONE
            binding?.llCheckout!!.visibility = View.VISIBLE
            binding?.rvCartItemsList!!.adapter = CartListAdapter(this, cartList)
            binding?.rvCartItemsList!!.layoutManager = LinearLayoutManager(this)
            binding?.rvCartItemsList!!.setHasFixedSize(true)

            var subTotal: Double = 0.0
            for (item in mCartList) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += price * quantity
                }
            }

            binding?.tvSubTotal!!.text = "$${subTotal}"
            binding?.tvShippingCharge!!.text = "$10.0"
            var total_charge = subTotal
            if (subTotal > 0) {
                total_charge = subTotal + 10
            }
            binding?.tvTotalAmount!!.text = "$${total_charge}"
        }
    }

    fun onSuccessGetAllProductList(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductList = productsList
    }
}