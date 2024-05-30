package com.fashionism.fashionismuserapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.db.ProductDetail
import com.fashionism.fashionismuserapp.databinding.ItemFashionBinding

class FashionDummyAdapter(
    private val list: List<ProductDetail>,
    private val favoritesMark: Boolean
) :
    RecyclerView.Adapter<FashionDummyAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ProductDetail)
    }

    class ViewHolder(private val binding: ItemFashionBinding, private val favoritesMark: Boolean) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductDetail) {
            Glide.with(itemView.context)
                .load(data.imageFashion)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.ivFashionImage)
            binding.tvFashionName.text = data.product
            binding.tvPrice.text = data.price
            binding.tvStoreName.text = data.storeName

            if (favoritesMark) {
                binding.ivFavoriteItem.visibility = View.GONE
                binding.ivFavoriteItemFill.visibility = View.VISIBLE
            } else {
                binding.ivFavoriteItem.visibility = View.VISIBLE
                binding.ivFavoriteItemFill.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFashionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, favoritesMark)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}