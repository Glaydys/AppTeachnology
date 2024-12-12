package com.example.myapplication

import android.util.Log
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun testRegisterLog() {
        val username = "0387179032"
        val email = "test@example.com"
        val password = "password123"
        val address = "123 Main St"

        onView(withId(R.id.editTextUsername)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.editTextAddress)).perform(typeText(address), closeSoftKeyboard())

        onView(withId(R.id.buttonRegister)).perform(click())

        Thread.sleep(1000)

        val logcatOutput = captureLogs()

        Assert.assertTrue(
            "Không tìm thấy log với thông điệp mong muốn!",
            logcatOutput.contains("Đăng ký thành công $username!")
        )
    }
    @Test
    fun testRegisterValid() {
        val username = "0387179036"
        val email = "test1@example.com"
        val password = "password123"
        val address = "1234 Main St"

        onView(withId(R.id.editTextUsername)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.editTextAddress)).perform(typeText(address), closeSoftKeyboard())

        onView(withId(R.id.buttonRegister)).perform(click())

        onView(withId(R.id.pagelogin)).check(matches(isDisplayed()))
    }
    private fun captureLogs(): String {
        val process = Runtime.getRuntime().exec("logcat -d")
        return process.inputStream.bufferedReader().use { it.readText() }
    }
}
