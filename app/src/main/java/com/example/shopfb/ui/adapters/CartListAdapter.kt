package com.example.shopfb.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopfb.models.CartItem
import com.example.shopfb.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart.view.*

class CartListAdapter(
    private var context: Context,
    private var cartList: List<CartItem>
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartList[position]
        if (holder is ViewHolder) {
            holder.itemView.tv_cart_item_title.text = cartItem.title
            holder.itemView.tv_cart_item_price.text = "$${cartItem.price}"
            holder.itemView.tv_cart_quantity.text = cartItem.cart_quantity
            GlideLoader(context).loadProductPicture(cartItem.image, holder.itemView.iv_cart_item_image)
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

}