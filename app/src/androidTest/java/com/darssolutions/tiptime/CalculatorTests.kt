package com.darssolutions.tiptime

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat

@RunWith(AndroidJUnit4::class)
class CalculatorTests {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    private fun getNumberFormat(value: Double): String {
        return try {
            NumberFormat.getCurrencyInstance().format(value)
        } catch (e: Exception) {
            "Number format error!"
        }
    }

    private fun interactWithUI(costOfService: Double, percentage: Double, roundUp: Boolean = true) {
        // Enter the cost of the service
        onView(withId(R.id.cost_of_service_edit_text))
            .perform(typeText(costOfService.toString()))
            .perform(ViewActions.closeSoftKeyboard())

        // Change the tip percentage
        when (percentage) {
            0.15 -> onView(withId(R.id.option_fifteen_percent))
                .perform(ViewActions.click())

            0.18 -> onView(withId(R.id.option_eighteen_percent))
                .perform(ViewActions.click())

            0.20 -> onView(withId(R.id.option_twenty_percent))
                .perform(ViewActions.click())

            else -> throw Exception("Invalid percentage!")
        }

        // Get Context of the app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Get value if round up is true
        val tipRounded: Double = if (roundUp) {
            kotlin.math.ceil(costOfService * percentage)
        } else {
            // Set the round up switch to false
            onView(withId(R.id.round_up_switch))
                .perform(ViewActions.click())
                .check(
                    matches(
                        ViewMatchers.isNotChecked()
                    )
                )

            costOfService * percentage
        }

        // Click on calculate button
        onView(withId(R.id.calculate_button))
            .perform(ViewActions.click())

        // Get the currency symbol of the device
        val tipCurrency = getNumberFormat(tipRounded)

        // Get the language of the device and get the translated text
        val expectedTipText = appContext.getString(R.string.tip_amount_with_value, tipCurrency)

        // Check the tip amount is correct
        onView(withId(R.id.tip_result))
            .check(matches(withText(expectedTipText)))
    }

    @Test
    fun calculate_20_percent_tip() {
        interactWithUI(100.0, 0.20)
    }

    @Test
    fun calculate_18_percent_tip() {
        interactWithUI(100.0, 0.18)
    }

    @Test
    fun calculate_15_percent_tip() {
        interactWithUI(100.0, 0.15)
    }

    @Test
    fun calculate_tip_for_zero() {
        // Enter the cost of the service
        onView(withId(R.id.cost_of_service_edit_text))
            .perform(typeText("0"))
            .perform(ViewActions.closeSoftKeyboard())

        // Click on calculate button
        onView(withId(R.id.calculate_button))
            .perform(ViewActions.click())

        // Check Snackbar is displayed
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.error_0_as_tip_value)))
    }

    @Test
    fun calculate_tip_for_empty() {
        // Click on calculate button
        onView(withId(R.id.calculate_button))
            .perform(ViewActions.click())

        // Check Snackbar is displayed
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.error_empty_cost)))
    }

    @Test
    fun calculate_tip_for_round_up() {
        interactWithUI(75.0, 0.15, true)
    }

    @Test
    fun calculate_tip_for_not_round_up() {
        interactWithUI(75.0, 0.15, false)
    }
}