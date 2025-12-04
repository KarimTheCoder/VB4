package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model

import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word

data class JustLearnedSessionData(
    val learnedWords: List<Word>,
    val mostMistakenWord: Word?
)

