package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: List<products>,
    private val onProductClick: (products) -> Unit // Ensure the type is explicitly defined
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)  // ImageView để hiển thị ảnh
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name_product
        holder.productPrice.text = "Price: ${product.price}"

        // Tải ảnh sản phẩm bằng Glide
        Glide.with(holder.productImage.context)
            .load("http://192.168.2.22:3000${product.image_product}") // Ghép URL server và đường dẫn hình ảnh
            .into(holder.productImage)


        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }


    override fun getItemCount(): Int {
        return productList.size
    }
}
