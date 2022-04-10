package com.example.shopfb.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopfb.R
import com.example.shopfb.databinding.FragmentDashboardBinding
import com.example.shopfb.models.Product
import com.example.shopfb.ui.activities.CartListActivity
import com.example.shopfb.ui.activities.SettingsActivity
import com.example.shopfb.ui.adapters.DashboardProductsListAdapter
import com.example.shopfb.utils.FirestoreClass

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardProductsList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDashboardProductsList() {
        showProgressDialog("Please wait")
        FirestoreClass().getDashboardProductsList(this)
    }

    fun onSuccessGetDashboardProductList(productsList: ArrayList<Product>) {
        hideProgressDialog()
        if (productsList.size > 0) {
            binding.tvNoProductInDashboard.visibility = View.GONE
            binding.rvDashboardItem.visibility = View.VISIBLE

            binding.rvDashboardItem.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItem.setHasFixedSize(true)
            binding.rvDashboardItem.adapter =
                DashboardProductsListAdapter(requireActivity(), productsList)
        } else {
            binding.tvNoProductInDashboard.visibility = View.VISIBLE
            binding.rvDashboardItem.visibility = View.GONE
        }
    }
}