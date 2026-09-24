package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.WordSelectionConfig
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SelectSessionWordsUseCaseTest {

    private lateinit var fakeRepository: FakeVocabularyRepository
    private lateinit var fakeDao: FakeWordProgressDao
    private lateinit var useCase: SelectSessionWordsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeVocabularyRepository()
        fakeDao = FakeWordProgressDao()
        useCase = SelectSessionWordsUseCase(fakeRepository, fakeDao)
    }

    @Test
    fun `when no unlearned words available, returns empty list`() = runTest {
        fakeRepository.words = emptyList()

        val config = WordSelectionConfig.default(level = "beginner")
        val result = useCase(config)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `filters out skipped words from session`() = runTest {
        fakeRepository.words = listOf(
            createWord(id = 1, name = "abandon"),
            createWord(id = 2, name = "abundant"),
            createWord(id = 3, name = "accumulate")
        )

        val config = WordSelectionConfig(
            level = "beginner",
            wordsPerSession = 5,
            skipWordIds = listOf(2)
        )
        val result = useCase(config)

        assertEquals(2, result.size)
        assertTrue(result.none { it.id == 2 })
        assertTrue(result.any { it.id == 1 })
        assertTrue(result.any { it.id == 3 })
    }

    @Test
    fun `respects wordsPerSession limit`() = runTest {
        fakeRepository.words = (1..10).map { id ->
            createWord(id = id, name = "word_$id")
        }

        val config = WordSelectionConfig(
            level = "beginner",
            wordsPerSession = 3
        )
        val result = useCase(config)

        assertEquals(3, result.size)
    }

    @Test
    fun `prioritizes words with higher mistake count`() = runTest {
        val word1 = createWord(id = 1, name = "easy_word")
        val word2 = createWord(id = 2, name = "hard_word")
        fakeRepository.words = listOf(word1, word2)

        // word2 has 5 mistakes, word1 has 0
        fakeDao.progressMap["IELTS_2"] = WordProgressEntity(
            id = 2,
            wordId = 2,
            source = "IELTS",
            mistakeCount = 5,
            familiarityScore = 0.1f
        )
        fakeDao.progressMap["IELTS_1"] = WordProgressEntity(
            id = 1,
            wordId = 1,
            source = "IELTS",
            mistakeCount = 0,
            familiarityScore = 0.8f
        )

        val config = WordSelectionConfig(
            level = "beginner",
            wordsPerSession = 1
        )
        val result = useCase(config)

        assertEquals(1, result.size)
        assertEquals(2, result.first().id)
        assertEquals("hard_word", result.first().word)
    }

    private fun createWord(id: Int, name: String): VocabularyWord {
        return VocabularyWord(
            id = id,
            word = name,
            translation = "trans_$name",
            source = VocabularySource.IELTS,
            level = "beginner"
        )
    }

    // ===== Fakes =====

    private class FakeVocabularyRepository : VocabularyRepository {
        var words: List<VocabularyWord> = emptyList()

        override suspend fun getVocabulary(level: String): List<VocabularyWord> = words
        override suspend fun getFavoriteWords(): List<VocabularyWord> = words.filter { it.isFavorite }
        override suspend fun getLearnedWords(level: String): List<VocabularyWord> = words.filter { it.isLearned }
        override suspend fun getUnlearnedWords(level: String): List<VocabularyWord> = words.filter { !it.isLearned }
        override suspend fun getAllUnlearnedWords(): List<VocabularyWord> = words.filter { !it.isLearned }
        override suspend fun getLearnedCount(level: String): Int = words.count { it.isLearned }
        override suspend fun getTotalCount(level: String): Int = words.size
        override suspend fun updateFavorite(source: VocabularySource, wordId: Int, isFavorite: Boolean) {}
        override suspend fun updateLearnState(source: VocabularySource, wordId: Int, isLearned: Boolean) {}
        override suspend fun getRandomWords(limit: Int, excludeIds: Set<Int>): List<VocabularyWord> {
            return words.filter { it.id !in excludeIds }.take(limit)
        }
    }

    private class FakeWordProgressDao : WordProgressDao {
        val progressMap = mutableMapOf<String, WordProgressEntity>()

        override suspend fun getBySource(source: String): List<WordProgressEntity> = progressMap.values.filter { it.source == source }
        override suspend fun getFavorites(source: String): List<WordProgressEntity> = progressMap.values.filter { it.source == source && it.isFavorite }
        override suspend fun getLearned(source: String): List<WordProgressEntity> = progressMap.values.filter { it.source == source && it.isLearned }
        override suspend fun getUnlearned(source: String): List<WordProgressEntity> = progressMap.values.filter { it.source == source && !it.isLearned }
        override suspend fun getBySourceAndWordId(source: String, wordId: Int): WordProgressEntity? = progressMap["${source}_$wordId"]
        override suspend fun updateFavorite(source: String, wordId: Int, isFavorite: Boolean) {}
        override suspend fun updateLearned(source: String, wordId: Int, isLearned: Boolean) {}
        override suspend fun updateBlacklisted(source: String, wordId: Int, isBlacklisted: Boolean) {}
        override suspend fun updateSkipped(source: String, wordId: Int, isSkipped: Boolean) {}
        override suspend fun insertAll(entities: List<WordProgressEntity>) {}
        override suspend fun insertAllIgnore(entities: List<WordProgressEntity>) {}
        override suspend fun getCount(source: String): Int = progressMap.size
        override suspend fun getLearnedCount(source: String): Int = progressMap.values.count { it.isLearned }
        override suspend fun getFavoriteCount(source: String): Int = progressMap.values.count { it.isFavorite }
        override suspend fun deleteBySource(source: String) {}
        override suspend fun getWordsForSession(source: String, limit: Int): List<WordProgressEntity> = progressMap.values.take(limit)
        override suspend fun incrementMistakeCount(source: String, wordId: Int) {}
        override suspend fun incrementCorrectCount(source: String, wordId: Int) {}
        override suspend fun updateFamiliarity(source: String, wordId: Int, score: Float, timestamp: Long) {}
        override suspend fun updateLastSeen(source: String, wordId: Int, timestamp: Long) {}
        override suspend fun updateNextReviewDate(source: String, wordId: Int, nextReviewDate: Long) {}
        override suspend fun getWordsDueForReview(source: String, currentTime: Long, limit: Int): List<WordProgressEntity> = emptyList()
        override suspend fun getMostMistakenWords(source: String, limit: Int): List<WordProgressEntity> = emptyList()
    }
}
