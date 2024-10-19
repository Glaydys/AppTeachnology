package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity

class run : AppCompatActivity() {

    private lateinit var horizontalScrollView: HorizontalScrollView
    private val handler = Handler(Looper.getMainLooper())
    private var currentImageIndex = 0 // Chỉ số hình ảnh hiện tại
    private val scrollDelay = 10000L // 10 giây
    private val imageWidth = 400 // Chiều rộng của mỗi hình ảnh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.banner) // Đảm bảo rằng bạn đã đặt đúng layout

        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        startAutoScroll()
    }

    private fun startAutoScroll() {
        handler.post(object : Runnable {
            override fun run() {
                // Cuộn đến hình ảnh tiếp theo
                horizontalScrollView.scrollTo(currentImageIndex * imageWidth, 0)

                // Cập nhật chỉ số hình ảnh
                currentImageIndex++

                // Nếu đã đến cuối hình ảnh, quay lại đầu
                if (currentImageIndex * imageWidth >= horizontalScrollView.getChildAt(0).width) {
                    currentImageIndex = 0
                }

                // Gọi lại hàm này sau một khoảng thời gian
                handler.postDelayed(this, scrollDelay)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Dừng cuộn khi Activity bị hủy
    }
}
