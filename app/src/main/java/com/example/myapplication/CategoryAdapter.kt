package com.example.myapplication

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Retrofit.category
import java.text.NumberFormat
import java.util.Locale

class CategoryAdapter(private val categories: List<category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDanhmuc: ImageView = view.findViewById(R.id.imgDanhmuc)
        val txtDanhmuc: TextView = view.findViewById(R.id.txtDanhmuc)
    }

    var onCategoryClick: (category) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.danhmuc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.txtDanhmuc.text = category.name_category

        Glide.with(holder.itemView.context)
            .load("http://$IP_ADDRESS:3000/${category.image_category}")
            .into(holder.imgDanhmuc)
        holder.itemView.setOnClickListener {
            onCategoryClick(category)
        }
    }

    override fun getItemCount() = categories.size
}
