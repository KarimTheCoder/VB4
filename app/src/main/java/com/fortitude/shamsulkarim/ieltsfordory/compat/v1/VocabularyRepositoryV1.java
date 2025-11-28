package com.fortitude.shamsulkarim.ieltsfordory.compat.v1;

import android.content.Context;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;

import java.util.List;

public class VocabularyRepositoryV1 {
    private final VocabularyRepository delegate;
    public VocabularyRepositoryV1(Context context){
        this.delegate = new VocabularyRepository(context);
    }
    public List<Word> getBeginnerVocabulary(){
        return delegate.getBeginnerVocabulary();
    }
    public List<Word> getIntermediateVocabulary(){
        return delegate.getIntermediateVocabulary();
    }
    public List<Word> getAdvanceVocabulary(){
        return delegate.getAdvanceVocabulary();
    }
}
