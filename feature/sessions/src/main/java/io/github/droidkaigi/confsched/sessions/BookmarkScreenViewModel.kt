package io.github.droidkaigi.confsched.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.droidkaigi.confsched.designsystem.strings.AppStrings
import io.github.droidkaigi.confsched.model.DroidKaigi2023Day
import io.github.droidkaigi.confsched.model.Filters
import io.github.droidkaigi.confsched.model.SessionsRepository
import io.github.droidkaigi.confsched.model.Timetable
import io.github.droidkaigi.confsched.model.TimetableItem
import io.github.droidkaigi.confsched.sessions.section.BookmarkSheetUiState
import io.github.droidkaigi.confsched.ui.UserMessageStateHolder
import io.github.droidkaigi.confsched.ui.buildUiState
import io.github.droidkaigi.confsched.ui.handleErrorAndRetry
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkScreenViewModel @Inject constructor(
    private val sessionsRepository: SessionsRepository,
    userMessageStateHolder: UserMessageStateHolder,
) : ViewModel() {

    private val sessionsStateFlow: StateFlow<Timetable> = sessionsRepository
        .getTimetableStream()
        .handleErrorAndRetry(
            AppStrings.Retry,
            userMessageStateHolder,
        )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Timetable(),
        )

    private val currentDayFilter = MutableStateFlow(
        DroidKaigi2023Day.entries.map { it },
    )

    private val allFilterChipSelected = MutableStateFlow(true)

    val uiState: StateFlow<BookmarkScreenUiState> =
        buildUiState(
            sessionsStateFlow,
            currentDayFilter,
            allFilterChipSelected,
        ) { sessions, currentDayFilter, allFilterChipSelected ->
            val sortAndGroupedBookmarkedTimetableItems = sessions.filtered(
                Filters(
                    days = currentDayFilter,
                    filterFavorite = true,
                ),
            ).timetableItems.groupBy {
                it.startsTimeString + it.endsTimeString
            }.mapValues { entries ->
                entries.value.sortedWith(
                    compareBy({ it.day?.name.orEmpty() }, { it.startsTimeString }),
                )
            }.toPersistentMap()

            if (sortAndGroupedBookmarkedTimetableItems.isEmpty()) {
                BookmarkScreenUiState(
                    contentUiState = BookmarkSheetUiState.Empty(
                        false,
                        currentDayFilter.toPersistentList(),
                    ),
                )
            } else {
                BookmarkScreenUiState(
                    contentUiState = BookmarkSheetUiState.ListBookmark(
                        sessions.bookmarks,
                        sortAndGroupedBookmarkedTimetableItems,
                        allFilterChipSelected,
                        currentDayFilter.toPersistentList(),
                    ),
                )
            }
        }

    fun onAllFilterChipClick() {
        currentDayFilter.update {
            DroidKaigi2023Day.entries.toList()
        }
        allFilterChipSelected.update {
            true
        }
    }

    fun onDayFirstChipClick() = onDayChipClick(DroidKaigi2023Day.Day1)

    fun onDaySecondChipClick() = onDayChipClick(DroidKaigi2023Day.Day2)

    fun onDayThirdChipClick() = onDayChipClick(DroidKaigi2023Day.Day3)

    private fun onDayChipClick(day: DroidKaigi2023Day) {
        currentDayFilter.update {
            when {
                it.size == DroidKaigi2023Day.entries.size && allFilterChipSelected.value -> {
                    listOf(day)
                }

                it.contains(day) -> if (it.size > 1) it.minus(day) else it

                else -> {
                    it.plus(day)
                }
            }
        }
        allFilterChipSelected.update {
            false
        }
    }

    fun onBookmarkClick(timetableItem: TimetableItem) {
        viewModelScope.launch {
            sessionsRepository.toggleBookmark(timetableItem.id)
        }
    }
}
