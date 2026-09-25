package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.data.sync.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.SelectSessionWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.testutil.FakeSharedPreferences
import com.fortitude.shamsulkarim.ieltsfordory.testutil.TestContextFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSp: FakeSharedPreferences
    private lateinit var appPreferences: AppPreferences
    private lateinit var fakeVocabRepo: FakeVocabularyRepository
    private lateinit var fakeWordProgressDao: FakeWordProgressDao
    private lateinit var fakeLearningRepo: FakeLearningRepository
    private lateinit var sessionWordsRepository: SessionWordsRepository
    private lateinit var selectSessionWordsUseCase: SelectSessionWordsUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSp = FakeSharedPreferences()
        val context = TestContextFactory.createContext(fakeSp)
        appPreferences = AppPreferences.get(context)

        // Generate 30 mock words
        val mockWords = (1..30).map { i ->
            VocabularyWord(
                id = i,
                word = "Word$i",
                translation = "Meaning$i",
                source = VocabularySource.IELTS,
                level = "beginner"
            )
        }
        fakeVocabRepo = FakeVocabularyRepository(mockWords)
        fakeWordProgressDao = FakeWordProgressDao()
        fakeLearningRepo = FakeLearningRepository()
        sessionWordsRepository = SessionWordsRepository()
        selectSessionWordsUseCase = SelectSessionWordsUseCase(fakeVocabRepo, fakeWordProgressDao)

        // Default words per session is 5
        appPreferences.setWordsPerSession(5)
        appPreferences.setRepeatationPerSession(5)

        viewModel = HomeViewModel(
            appPreferences = appPreferences,
            selectSessionWordsUseCase = selectSessionWordsUseCase,
            learningRepository = fakeLearningRepo,
            sessionWordsRepository = sessionWordsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load loads words according to wordsPerSession setting`() = runTest {
        advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.words.size)
        assertTrue(viewModel.uiState.value.infoBannerText.contains("5 new words"))
    }

    @Test
    fun `checkForRefresh reloads words when wordsPerSession changed in preferences`() = runTest {
        advanceUntilIdle()
        assertEquals(5, viewModel.uiState.value.words.size)

        // User changes words per session to 15
        appPreferences.setWordsPerSession(15)

        viewModel.checkForRefresh()
        advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.words.size)
        assertTrue(viewModel.uiState.value.infoBannerText.contains("15 new words"))
    }

    @Test
    fun `checkForRefresh reloads words when repeatationPerSession changed in preferences`() = runTest {
        advanceUntilIdle()

        // Prepare session with current words
        val prepared = viewModel.prepareSession()
        assertTrue(prepared)
        assertTrue(sessionWordsRepository.hasSessionWords())

        // User changes repetition per session to 10
        appPreferences.setRepeatationPerSession(10)

        viewModel.checkForRefresh()
        advanceUntilIdle()

        // The session words cache should have been cleared and words reloaded
        assertFalse(sessionWordsRepository.hasSessionWords())
    }

    @Test
    fun `checkForRefresh reloads when sessionWordsRepository requested refresh`() = runTest {
        advanceUntilIdle()

        sessionWordsRepository.requestHomeRefresh()
        viewModel.checkForRefresh()
        advanceUntilIdle()

        assertFalse(sessionWordsRepository.consumeRefreshRequest())
    }

    @Test
    fun `prepareSession resets and returns false if settings changed before starting`() = runTest {
        advanceUntilIdle()

        // Setting changed externally before prepareSession was called
        appPreferences.setWordsPerSession(10)

        val prepared = viewModel.prepareSession()
        assertFalse(prepared)

        advanceUntilIdle()
        // Words should now be reloaded with 10 words
        assertEquals(10, viewModel.uiState.value.words.size)
    }

    // --- Fakes ---

    private class FakeVocabularyRepository(
        var words: List<VocabularyWord> = emptyList()
    ) : VocabularyRepository {
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

    private class FakeLearningRepository : LearningRepository {
        override suspend fun getFavLearnedState(userName: String): FavLearnedState = FavLearnedState()
        override suspend fun fetchSessionWords(level: String, wordsPerSession: Int): List<VocabularyWord> = emptyList()
        override suspend fun getAllUnlearnedWords(level: String): List<VocabularyWord> = emptyList()
        override suspend fun updateLearnedStatus(words: List<VocabularyWord>) {}
        override suspend fun updateJustLearnedStatus(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int) {}
        override suspend fun updateFavoriteStatus(word: VocabularyWord, newStatus: Boolean) {}
        override suspend fun updateLearnedStatus(word: VocabularyWord, newStatus: Boolean) {}
        override suspend fun getJustLearnedSessionData(level: String): JustLearnedSessionData = JustLearnedSessionData(emptyList(), null)
        override suspend fun recordCorrectAnswer(word: VocabularyWord) {}
        override suspend fun recordMistake(word: VocabularyWord) {}
        override suspend fun updateFamiliarityScore(word: VocabularyWord, score: Float) {}
        override suspend fun skipWord(word: VocabularyWord) {}
        override suspend fun updateLastSeen(word: VocabularyWord) {}
        override suspend fun updateNextReviewDate(word: VocabularyWord, nextReviewDate: Long) {}
    }
}
