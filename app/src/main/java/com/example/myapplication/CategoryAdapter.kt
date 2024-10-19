package com.example.myapplication

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CategoryAdapter(private val categories: List<category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDanhmuc: ImageView = view.findViewById(R.id.imgDanhmuc)
        val txtDanhmuc: TextView = view.findViewById(R.id.txtDanhmuc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.danhmuc, parent, false)
        return ViewHolder(view)
    }

    fun getImageResourceId(name: String): Int {
        return when (name) {
            "Điện thoại" -> R.drawable.img1
            "Máy tính bảng" -> R.drawable.img6
            "Laptop" -> R.drawable.img2
            "Đồng hồ" -> R.drawable.img3
            "Máy tính để bàn" -> R.drawable.img4
            "Máy in" -> R.drawable.img7
            "Máy ảnh" -> R.drawable.img5
            else -> R.drawable.img
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.txtDanhmuc.text = category.name_category
        holder.imgDanhmuc.setImageResource(getImageResourceId(category.name_category))
    }



    override fun getItemCount() = categories.size
}
