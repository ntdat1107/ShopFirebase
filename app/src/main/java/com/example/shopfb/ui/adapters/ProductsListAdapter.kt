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
import com.example.shopfb.ui.fragments.ProductsFragment
import com.example.shopfb.utils.Constants
import com.example.shopfb.utils.GlideLoader
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsListAdapter(
    private var context: Context,
    private var productsList: ArrayList<Product>,
    private val fragment: ProductsFragment
) :
    RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemProduct = productsList[position]

        GlideLoader(context).loadProductPicture(itemProduct.image, holder.itemView.iv_item_image)
        holder.itemView.tv_item_name.text = itemProduct.title
        holder.itemView.tv_item_price.text = "$ ${itemProduct.price}"
        holder.itemView.ib_delete_product.setOnClickListener {
            fragment.deleteProduct(itemProduct.product_id)
        }

        holder.itemView.setOnClickListener {
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