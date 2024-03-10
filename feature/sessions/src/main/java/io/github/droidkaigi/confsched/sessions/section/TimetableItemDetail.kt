package io.github.droidkaigi.confsched.sessions.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.model.Lang
import io.github.droidkaigi.confsched.model.TimetableItem
import io.github.droidkaigi.confsched.sessions.component.TimetableItemDetailContent
import io.github.droidkaigi.confsched.sessions.component.TimetableItemDetailSummaryCard

data class TimetableItemDetailSectionUiState(
    val timetableItem: TimetableItem,
)

@Composable
internal fun TimetableItemDetail(
    uiState: TimetableItemDetailSectionUiState,
    contentPadding: PaddingValues,
    selectedLanguage: Lang?,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            TimetableItemDetailSummaryCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                timetableItem = uiState.timetableItem,
            )
        }

        item {
            TimetableItemDetailContent(
                uiState = uiState.timetableItem,
                selectedLanguage = selectedLanguage,
                onLinkClick = onLinkClick,
            )
        }
    }
}
