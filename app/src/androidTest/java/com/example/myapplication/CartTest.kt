package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.Retrofit.products
import org.junit.Assert


@RunWith(AndroidJUnit4::class)
class CartTest {

    @Test
    fun testBuyNowButtonClickLogsMessage() {
        val sharedPreferences = InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("isLoggedIn", true)
        editor.putString("_id", "675a9c25e1aa0c89eb1e1e41")
        editor.apply()

        val userId = sharedPreferences.getString("_id", null)
        Log.d("Test", "User ID: $userId")

        val product = products(
            _id = "67089d2909136ebff0d348d4",
            name_product = "Samsung Galaxy Tab 9",
            price = 20200000.toString(),
            description = """
                    Kích thước màn hình: 11 inches
                    Công nghệ màn hình: Dynamic AMOLED 2X
                    Camera sau: 13 MP
                    Camera trước: Camera góc siêu rộng 12 MP
                    Chipset: Snapdragon® 8 Gen 2
                    Dung lượng RAM: 8 GB
                    Bộ nhớ trong: 128 GB
                    Pin: 8,400mAh
                    Hệ điều hành: Android 13
                """.trimIndent(),
            image_product = "/uploads/1729501051989_tabs9.jpg",
            category_id = 2,
            quantity = 100,
            rate = 5.0f,
            totalComments = 6
        )

        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ProductDetails::class.java)
        intent.putExtra("product", product)

        ActivityScenario.launch<ProductDetails>(intent)

        onView(withId(R.id.buynow)).perform(ViewActions.click())

        val logcatOutput = captureLogs()

        Log.d("Test", "Captured Logs: $logcatOutput")

        Assert.assertTrue(
            "Không tìm thấy log với thông điệp mong muốn!",
            logcatOutput.contains("Thêm vào giỏ hàng thành công")
        )
    }

    //  truy xuất log từ logcat
    private fun captureLogs(): String {
        val process = Runtime.getRuntime().exec("logcat -d -s ProductDetails")
        return process.inputStream.bufferedReader().use { it.readText() }
    }
}
