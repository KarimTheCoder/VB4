package com.fortitude.shamsulkarim.ieltsfordory.domain.repository;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import java.util.List;

public interface VocabularyRepositoryContract {
    List<Word> getBeginnerVocabulary();
    List<Word> getIntermediateVocabulary();
    List<Word> getAdvanceVocabulary();
}
