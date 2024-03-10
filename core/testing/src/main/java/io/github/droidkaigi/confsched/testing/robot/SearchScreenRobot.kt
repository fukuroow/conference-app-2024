package io.github.droidkaigi.confsched.testing.robot

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.droidkaigi.confsched.designsystem.theme.KaigiTheme
import io.github.droidkaigi.confsched.sessions.SearchScreen
import io.github.droidkaigi.confsched.sessions.SearchScreenSearchTextFiledTestTag
import io.github.droidkaigi.confsched.sessions.SearchScreenTestTag
import io.github.droidkaigi.confsched.sessions.component.DropdownFilterChipItemTestTag
import io.github.droidkaigi.confsched.sessions.section.SearchFilterTestTag
import io.github.droidkaigi.confsched.testing.RobotTestRule
import io.github.droidkaigi.confsched.testing.coroutines.runTestWithLogging
import kotlinx.coroutines.test.TestDispatcher
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class SearchScreenRobot @Inject constructor(
    private val testDispatcher: TestDispatcher,
) {
    @Inject lateinit var robotTestRule: RobotTestRule

    private lateinit var composeTestRule: AndroidComposeTestRule<*, *>

    operator fun invoke(
        block: SearchScreenRobot.() -> Unit,
    ) {
        runTestWithLogging(timeout = 30.seconds) {
            this@SearchScreenRobot.composeTestRule = robotTestRule.composeTestRule
            block()
        }
    }

    fun setupSearchScreenContent() {
        composeTestRule.setContent {
            KaigiTheme {
                SearchScreen(
                    onBackClick = {},
                    onTimetableItemClick = {},
                )
            }
        }
        waitUntilIdle()
    }

    fun clickFilterChip(filterChipTestTag: String) {
        composeTestRule
            .onNode(hasTestTag(filterChipTestTag))
            .performClick()
        waitUntilIdle()
    }

    fun scrollSearchFilterToLeft() {
        composeTestRule
            .onNode(hasTestTag(SearchFilterTestTag))
            .performTouchInput {
                swipeLeft(
                    startX = visibleSize.width.toFloat(),
                    endX = visibleSize.width / 2f,
                )
            }
    }

    fun clickFirstDropdownMenuItem() {
        composeTestRule
            .onAllNodes(hasTestTag(DropdownFilterChipItemTestTag))
            .onFirst()
            .performClick()
        waitUntilIdle()
    }

    fun clickLastDropdownMenuItem() {
        composeTestRule
            .onAllNodes(hasTestTag(DropdownFilterChipItemTestTag))
            .onLast()
            .performClick()
        waitUntilIdle()
    }

    fun inputDummyTextSearchTextFieldAppBar() {
        composeTestRule
            .onNodeWithTag(SearchScreenSearchTextFiledTestTag)
            .performTextInput("abcdefg")
        waitUntilIdle()
    }

    fun checkScreenCapture() {
        composeTestRule
            .onNode(isRoot())
            .captureRoboImage()
    }

    fun checkSearchScreenCapture() {
        composeTestRule
            .onNode(hasTestTag(SearchScreenTestTag))
            .captureRoboImage()
    }

    fun waitUntilIdle() {
        composeTestRule.waitForIdle()
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
