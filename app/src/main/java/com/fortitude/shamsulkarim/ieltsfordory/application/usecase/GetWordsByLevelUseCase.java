package com.fortitude.shamsulkarim.ieltsfordory.application.usecase;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.domain.model.Level;
import com.fortitude.shamsulkarim.ieltsfordory.domain.repository.VocabularyRepositoryContract;

import java.util.List;

public class GetWordsByLevelUseCase {
    private final VocabularyRepositoryContract repo;
    public GetWordsByLevelUseCase(VocabularyRepositoryContract repo){
        this.repo = repo;
    }
    public List<Word> execute(Level level) {
        switch (level) {
            case BEGINNER:
                return repo.getBeginnerVocabulary();
            case INTERMEDIATE:
                return repo.getIntermediateVocabulary();
            case ADVANCE:
            default:
                return repo.getAdvanceVocabulary();
        }
    }
}
