package com.example.shopfb.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopfb.R
import com.example.shopfb.models.Product
import com.example.shopfb.ui.activities.ProductDetailsActivity
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard.view.*

class DashboardProductsListAdapter(
    private var context: Context,
    private var productsList: ArrayList<Product>
) :
    RecyclerView.Adapter<DashboardProductsListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_dashboard, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = productsList[position]
        GlideLoader(context).loadProductPicture(
            itemProduct.image,
            holder.itemView.iv_dashboard_product_image
        )

        holder.itemView.tv_dashboard_product_title.text = itemProduct.title
        holder.itemView.tv_dashboard_product_price.text = "$${itemProduct.price}"

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, itemProduct.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, itemProduct.user_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}