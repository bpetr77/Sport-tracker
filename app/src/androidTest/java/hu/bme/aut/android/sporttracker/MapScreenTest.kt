package hu.bme.aut.android.sporttracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MapScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun map_isDisplayed() {
        composeTestRule.setContent { MapScreenTestHost() }
        assertEquals(4, 2 + 2)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
    }

    @Test
    fun fabRoutePlanner_opensRoutePlannerSheet() {
        composeTestRule.setContent { MapScreenTestHost() }

        composeTestRule.onNodeWithTag("fab_route_planner").performClick()


        composeTestRule.onNodeWithTag("route_planner").assertIsDisplayed()
    }

    @Test
    fun fabStart_opensBottomSheet() {
        composeTestRule.setContent { MapScreenTestHost() }

        composeTestRule.onNodeWithTag("fab_start").performClick()


        composeTestRule.onNodeWithTag("bottom_sheet").assertExists()    }

    @Test
    fun routePlannerFields_showCorrectText() {
        composeTestRule.setContent { MapScreenTestHost() }

        // Megnyitjuk
        composeTestRule.onNodeWithTag("fab_route_planner").performClick()

        // Mivel a test host fake, kézzel triggerelhetjük a state-et
        composeTestRule.runOnUiThread {
            // Ez itt mutatja a state tesztelhetőségét
        }


        composeTestRule.onNodeWithTag("from_field").assertExists()
        composeTestRule.onNodeWithTag("to_field").assertExists()
    }
}