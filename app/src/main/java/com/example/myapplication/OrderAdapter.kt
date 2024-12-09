package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Retrofit.Order
import com.example.myapplication.Retrofit.OrderDetail
import com.example.myapplication.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderAdapter(
    private var orderList: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    // Cập nhật lại phương thức onBindViewHolder
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.orderId.text = order.OrderCode
        holder.orderDate.text = order.OrderDate
        holder.orderStatus.text = order.Status
        holder.totalAmount.text = "Tổng tiền: ${order.TotalAmount} VND"

        holder.detailsContainer.visibility = View.GONE

        holder.viewDetailsButton.setOnClickListener {
            val orderId = order._id.toString()

            // Kiểm tra nếu orderId hợp lệ
            if (!orderId.isNullOrEmpty()) {
                getOrderDetails(orderId, holder) { orderDetails ->
                    if (orderDetails != null && orderDetails.isNotEmpty()) {
                        val details = orderDetails.joinToString("\n") { orderDetail ->
                                    "Tên sản phẩm: ${orderDetail.productId.name_product}\n" +
                                    "Số lượng: ${orderDetail.quantity}\n" +
                                    "Giá: ${orderDetail.productId.price} VND\n"
                        }
                        holder.detailsText.text = details
                        holder.detailsContainer.visibility = View.VISIBLE
                    } else {
                        holder.detailsContainer.visibility = View.GONE
                    }
                }
            } else {
                Log.e("OrderAdapter", "Order ID is null or empty")
            }
        }

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = orderList.size

    fun updateData(newOrderList: List<Order>) {
        orderList = newOrderList
        notifyDataSetChanged()
    }

    // Hàm gọi API để lấy chi tiết đơn hàng
    private fun getOrderDetails(orderId: String, holder: OrderViewHolder, callback: (List<OrderDetail>?) -> Unit) {
        val apiService = RetrofitClient.apiService

        apiService.getOrderDetails(orderId).enqueue(object : Callback<List<OrderDetail>> {
            override fun onResponse(call: Call<List<OrderDetail>>, response: Response<List<OrderDetail>>) {
                if (response.isSuccessful) {
                    val orderDetails = response.body()
                    callback(orderDetails)
                } else {
                    Log.e("API_ERROR", "Error fetching order details: ${response.code()} ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<OrderDetail>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to get order details: ${t.message}")
                callback(null)
            }
        })
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val orderDate: TextView = itemView.findViewById(R.id.order_date)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status)
        val totalAmount: TextView = itemView.findViewById(R.id.order_total_amount)
        val viewDetailsButton: TextView = itemView.findViewById(R.id.view_details_button)
        val detailsContainer: LinearLayout = itemView.findViewById(R.id.details_container)
        val detailsText: TextView = itemView.findViewById(R.id.details_text)
    }
}
