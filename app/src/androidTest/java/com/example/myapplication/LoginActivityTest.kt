package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @JvmField
    @Rule
    val activityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginValidCredentials() {
        onView(withId(R.id.editTextUsername)).perform(typeText("0387179030"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("3011"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin)).perform(click())

        onView(withId(R.id.trangchu)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginInvalidCredentials() {
        onView(withId(R.id.editTextUsername)).perform(typeText("0387179030"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("password13"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin)).perform(click())

        val logcatOutput = getLogcatOutput()

        Assert.assertTrue(logcatOutput.contains("Lỗi: Wrong password"))
    }
    private fun getLogcatOutput(): String {
        val process = Runtime.getRuntime().exec("logcat -d")
        val reader = process.inputStream.bufferedReader()
        return reader.readText()
    }

    @Test
    fun testSharedPreferencesAfterLogin() {
        onView(withId(R.id.editTextUsername)).perform(typeText("0387179030"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("3011"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin)).perform(click())

        Espresso.onIdle()

        val sharedPreferences: SharedPreferences = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        assertTrue("User should be logged in", isLoggedIn)

        val username = sharedPreferences.getString("username", null)
        assertNotNull("Username should be saved", username)
    }
}
