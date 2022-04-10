package com.example.shopfb.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopfb.R
import com.example.shopfb.databinding.FragmentProductsBinding
import com.example.shopfb.models.Product
import com.example.shopfb.ui.activities.AddProductActivity
import com.example.shopfb.ui.adapters.ProductsListAdapter
import com.example.shopfb.utils.FirestoreClass

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getProductsList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.products_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProductsList() {
        showProgressDialog("Please wait")
        FirestoreClass().getProductsList(this)
    }

    fun onSuccessGetProductList(productsList: ArrayList<Product>) {
        hideProgressDialog()

        if (productsList.size > 0) {
            binding.tvNoProduct.visibility = View.GONE
            binding.rvProductsItem.visibility = View.VISIBLE

            binding.rvProductsItem.layoutManager = LinearLayoutManager(activity)
            binding.rvProductsItem.setHasFixedSize(true)
            binding.rvProductsItem.adapter =
                ProductsListAdapter(requireActivity(), productsList, this)
        } else {
            binding.tvNoProduct.visibility = View.VISIBLE
            binding.rvProductsItem.visibility = View.GONE
        }
    }

    private fun showAlertDialog(productID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete")
            .setMessage("Are you sure about that?")
            .setIcon(R.drawable.ic_vector_alert)
            .setPositiveButton("Yes") { _, _ ->
                showProgressDialog("Please wait")
                FirestoreClass().deleteProduct(this, productID)
            }
            .setNegativeButton("No") { it, _ ->
                it.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun deleteProduct(productID: String) {
        showAlertDialog(productID)
    }

    fun onSuccessDeleteProduct() {
        hideProgressDialog()
        Toast.makeText(requireActivity(), "Delete product successfully", Toast.LENGTH_SHORT).show()
        getProductsList()
    }
}