package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

data class JustLearnedSessionData(
    val learnedWords: List<VocabularyWord>,
    val mostMistakenWord: VocabularyWord?
)


