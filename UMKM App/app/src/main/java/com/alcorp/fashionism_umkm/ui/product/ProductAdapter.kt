package com.alcorp.fashionism_umkm.ui.product

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.data.remote.response.ProductData
import com.alcorp.fashionism_umkm.databinding.ItemProductBinding
import com.alcorp.fashionism_umkm.ui.product.ProductFragment.Companion.PRODUCT_LIST_RESULT
import com.alcorp.fashionism_umkm.ui.product.detail_product.DetailProductActivity
import com.alcorp.fashionism_umkm.ui.product.detail_product.DetailProductActivity.Companion.EXTRA_DETAIL_PRODUCT
import com.bumptech.glide.Glide


class ProductAdapter(private val listOufit: List<ProductData>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(listOufit[position]) {
                Glide.with(itemView.context)
                    .load(this.product_image)
                    .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.default_image))
                    .error(ContextCompat.getDrawable(itemView.context, R.drawable.default_image))
                    .into(binding.ivProduct)

                binding.tvOutfitTitle.text = this.name
                binding.tvOutfitPrice.text = this.price

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailProductActivity::class.java)
                    intent.putExtra(EXTRA_DETAIL_PRODUCT, this.id.toString())
                    (itemView.context as Activity).startActivityForResult(intent, PRODUCT_LIST_RESULT)
                }
            }
        }
    }

    override fun getItemCount(): Int = listOufit.size

    inner class ViewHolder(val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)
}