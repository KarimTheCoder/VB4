package com.fortitude.shamsulkarim.ieltsfordory.data.vocabulary

import android.content.Context
import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.data_old.source.GREDataSource
import com.fortitude.shamsulkarim.ieltsfordory.data_old.source.IELTSDataSource
import com.fortitude.shamsulkarim.ieltsfordory.data_old.source.SATDataSource
import com.fortitude.shamsulkarim.ieltsfordory.data_old.source.TOEFLDataSource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class AggregatedVocabularyRepository(context: Context) : VocabularyRepository {
    private val ielts = IELTSDataSource(context)
    private val toefl = TOEFLDataSource(context)
    private val sat = SATDataSource(context)
    private val gre = GREDataSource(context)
    private val pool = Executors.newFixedThreadPool(4)

    override fun getVocabulary(level: String): List<Word> {
        val tasks = mutableListOf<Callable<List<Word>>>()
        when {
            level.equals("beginner", true) -> {
                tasks.add(Callable { ielts.getBeginnerWords() })
                tasks.add(Callable { toefl.getBeginnerWords() })
                tasks.add(Callable { sat.getBeginnerWords() })
                tasks.add(Callable { gre.getBeginnerWords() })
            }
            level.equals("intermediate", true) -> {
                tasks.add(Callable { ielts.getIntermediateWords() })
                tasks.add(Callable { toefl.getIntermediateWords() })
                tasks.add(Callable { sat.getIntermediateWords() })
                tasks.add(Callable { gre.getIntermediateWords() })
            }
            else -> {
                tasks.add(Callable { ielts.getAdvanceWords() })
                tasks.add(Callable { toefl.getAdvanceWords() })
                tasks.add(Callable { sat.getAdvanceWords() })
                tasks.add(Callable { gre.getAdvanceWords() })
            }
        }
        return invokeAndMerge(tasks)
    }

    override fun getFavoriteWords(): List<Word> {
        val words = mutableListOf<Word>()
        words.addAll(ielts.favoriteWords)
        words.addAll(toefl.favoriteWords)
        words.addAll(sat.favoriteWords)
        words.addAll(gre.favoriteWords)
        return words
    }

    override fun getLearnedWords(level: String): List<Word> {
        val v = "True"
        return when {
            level.equals("beginner", true) -> ielts.getBeginnerFilteredWords(v) + toefl.getBeginnerFilteredWords(v) + sat.getBeginnerFilteredWords(v) + gre.getBeginnerFilteredWords(v)
            level.equals("intermediate", true) -> ielts.getIntermediateFilteredWords(v) + toefl.getIntermediateFilteredWords(v) + sat.getIntermediateFilteredWords(v) + gre.getIntermediateFilteredWords(v)
            else -> ielts.getAdvanceFilteredWords(v) + toefl.getAdvanceFilteredWords(v) + sat.getAdvanceFilteredWords(v) + gre.getAdvanceFilteredWords(v)
        }
    }

    override fun getUnlearnedWords(level: String): List<Word> {
        val v = "False"
        return when {
            level.equals("beginner", true) -> ielts.getBeginnerFilteredWords(v) + toefl.getBeginnerFilteredWords(v) + sat.getBeginnerFilteredWords(v) + gre.getBeginnerFilteredWords(v)
            level.equals("intermediate", true) -> ielts.getIntermediateFilteredWords(v) + toefl.getIntermediateFilteredWords(v) + sat.getIntermediateFilteredWords(v) + gre.getIntermediateFilteredWords(v)
            else -> ielts.getAdvanceFilteredWords(v) + toefl.getAdvanceFilteredWords(v) + sat.getAdvanceFilteredWords(v) + gre.getAdvanceFilteredWords(v)
        }
    }

    override fun getLearnedCount(level: String): Int {
        val v = "True"
        return when {
            level.equals("beginner", true) -> ielts.getBeginnerFilteredWords(v).size + toefl.getBeginnerFilteredWords(v).size + sat.getBeginnerFilteredWords(v).size + gre.getBeginnerFilteredWords(v).size
            level.equals("intermediate", true) -> ielts.getIntermediateFilteredWords(v).size + toefl.getIntermediateFilteredWords(v).size + sat.getIntermediateFilteredWords(v).size + gre.getIntermediateFilteredWords(v).size
            else -> ielts.getAdvanceFilteredWords(v).size + toefl.getAdvanceFilteredWords(v).size + sat.getAdvanceFilteredWords(v).size + gre.getAdvanceFilteredWords(v).size
        }
    }

    override fun getTotalCount(level: String): Int {
        return when {
            level.equals("beginner", true) -> ielts.beginnerWordCount + toefl.beginnerWordCount + sat.beginnerWordCount + gre.beginnerWordCount
            level.equals("intermediate", true) -> ielts.intermediateWordCount + toefl.intermediateWordCount + sat.intermediateWordCount + gre.intermediateWordCount
            else -> ielts.advanceWordCount + toefl.advanceWordCount + sat.advanceWordCount + gre.advanceWordCount
        }
    }

    override fun updateFavorite(source: String, id: String, isFavorite: String) {
        when (source.uppercase()) {
            "IELTS" -> ielts.updateFavorite(id, isFavorite)
            "TOEFL" -> toefl.updateFavorite(id, isFavorite)
            "SAT" -> sat.updateFavorite(id, isFavorite)
            "GRE" -> gre.updateFavorite(id, isFavorite)
        }
    }

    override fun updateLearnState(source: String, id: String, isLearned: String) {
        when (source.uppercase()) {
            "IELTS" -> ielts.updateLearnState(id, isLearned)
            "TOEFL" -> toefl.updateLearnState(id, isLearned)
            "SAT" -> sat.updateLearnState(id, isLearned)
            "GRE" -> gre.updateLearnState(id, isLearned)
        }
    }

    private fun invokeAndMerge(tasks: List<Callable<List<Word>>>): List<Word> {
        return try {
            val futures = pool.invokeAll(tasks)
            val merged = mutableListOf<Word>()
            for (f in futures) merged.addAll(f.get())
            merged
        } catch (e: Exception) {
            val merged = mutableListOf<Word>()
            tasks.forEach { t ->
                try { merged.addAll(t.call()) } catch (_: Exception) {}
            }
            merged
        }
    }
}

