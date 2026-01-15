package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.ui.components.WordCard

@Composable
fun WordList(
    words: List<VocabularyWord>,
    listState: LazyListState,
    loadingAudioWord: String?,
    onFavoriteClick: (VocabularyWord) -> Unit,
    onSpeakerClick: (VocabularyWord) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = words,
            key = { it.word }
        ) { word ->
            WordCard(
                word = word,
                onFavoriteClick = { onFavoriteClick(word) },
                onSpeakerClick = { onSpeakerClick(word) },
                isAudioLoading = loadingAudioWord == word.word
            )
        }
    }
}
