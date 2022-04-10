package com.example.shopfb.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.shopfb.models.CartItem
import com.example.shopfb.models.Product
import com.example.shopfb.models.User
import com.example.shopfb.ui.activities.*
import com.example.shopfb.ui.fragments.DashboardFragment
import com.example.shopfb.ui.fragments.ProductsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FirestoreClass {
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun getCurrentUserID(): String {
        var currentUserID = ""
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        db.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
//                Log.i(activity.javaClass.simpleName, it.toString())
                val user = it.toObject(User::class.java)!!
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.LOGGED_IN_USERNAME,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}"
                )
                    .apply()


                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.getUserDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun getProductDetailsByID(activity: Activity, productID: String) {
        db.collection(Constants.PRODUCTS)
            .document(productID)
            .get()
            .addOnSuccessListener {
//                Log.i("Product response:", it.toString())
                val product: Product = it.toObject(Product::class.java)!!
                when (activity) {
                    is ProductDetailsActivity -> {
                        activity.onSuccessGetProduct(product)
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is BaseActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserProfile(activity: Activity, userHashMap: HashMap<String, Any>) {
        db.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                        activity.showErrorSnackBar(e.toString(), true)
                    }
                }
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val sRef = storage.reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!)
            .addOnSuccessListener {
//                Log.e(
//                    "Firebase Image URL",
//                    it.metadata!!.reference!!.downloadUrl.toString()
//                )
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { Uri ->
//                    Log.e("Downloadable Image URL", Uri.toString())
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(Uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(Uri.toString())
                        }
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
//                Log.e(
//                    activity.javaClass.simpleName,
//                    it.message,
//                    it
//                )
            }

    }

    fun uploadProductDetails(activity: Activity, product: Product) {
        db.collection(Constants.PRODUCTS)
            .document()
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                when (activity) {
                    is AddProductActivity -> {
                        activity.uploadProductSuccess()
                    }
                }
            }
    }

    fun getProductsList(fragment: Fragment) {
        db.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
//                Log.e("Products list", it.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.onSuccessGetProductList(productsList)
                    }
                }
            }
    }

    fun getDashboardProductsList(fragment: Fragment) {
        db.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
//                Log.e("Products list", it.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (fragment) {
                    is DashboardFragment -> {
                        fragment.onSuccessGetDashboardProductList(productsList)
                    }
                }
            }
//            .addOnFailureListener {
//                when (fragment) {
//                    is DashboardFragment -> {
//                        fragment.hideProgressDialog()
//                    }
//                }
//            }
    }

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        db.collection(Constants.PRODUCTS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.onSuccessDeleteProduct()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
//                Log.e(
//                    fragment.requireActivity().javaClass.simpleName,
//                    "Error while deleting product",
//                    e
//                )
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, cartItem: CartItem) {
        db.collection(Constants.CART_ITEMS)
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                // Do something
                activity.onAddToCartSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                // Do something
            }
    }

    fun checkIfProductInCart(activity: ProductDetailsActivity, productID: String) {
        db.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productID)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                // Do something
            }
    }

    fun getCartList(activity: BaseActivity) {
        db.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val cartList: ArrayList<CartItem> = ArrayList()

                for (i in it.documents) {
                    val cartItem = i.toObject(CartItem::class.java)
                    cartItem!!.id = i.id
                    cartList.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.onSuccessGetCartList(cartList)
                    }
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getAllProducts(activity: BaseActivity) {
        db.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
//                Log.e("Products list", it.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.onSuccessGetAllProductList(productsList)
                    }
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

}