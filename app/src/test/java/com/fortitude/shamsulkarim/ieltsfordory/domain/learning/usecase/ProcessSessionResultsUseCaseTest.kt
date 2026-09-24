package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import com.fortitude.shamsulkarim.ieltsfordory.data.sync.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionResult
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.WordQuizResult
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProcessSessionResultsUseCaseTest {

    private lateinit var fakeRepository: FakeLearningRepository
    private lateinit var useCase: ProcessSessionResultsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeLearningRepository()
        useCase = ProcessSessionResultsUseCase(fakeRepository)
    }

    private fun createWord(id: Int, familiarity: Double, isLearned: Boolean = false): VocabularyWord {
        return VocabularyWord(
            id = id,
            word = "word_$id",
            translation = "trans_$id",
            source = VocabularySource.IELTS,
            level = "beginner",
            familiarityScore = familiarity,
            isLearned = isLearned,
            isFavorite = false
        )
    }

    @Test
    fun `correct answer increases familiarity score by 0_1`() = runTest {
        val word = createWord(id = 1, familiarity = 0.5)
        val sessionResult = SessionResult(
            wordResults = listOf(WordQuizResult(word = word, wasCorrect = true)),
            totalCorrect = 1,
            totalMistakes = 0,
            grade = "A+",
            gradeProgress = 1f,
            congratsTitle = "Well Done",
            congratsMessage = "Great job"
        )

        useCase(sessionResult)

        assertEquals(0.6f, fakeRepository.familiarityUpdates[1] ?: 0f, 0.01f)
    }

    @Test
    fun `incorrect answer decreases familiarity score by 0_2`() = runTest {
        val word = createWord(id = 2, familiarity = 0.5)
        val sessionResult = SessionResult(
            wordResults = listOf(WordQuizResult(word = word, wasCorrect = false)),
            totalCorrect = 0,
            totalMistakes = 1,
            grade = "F",
            gradeProgress = 0f,
            congratsTitle = "Keep Practicing",
            congratsMessage = "Try again"
        )

        useCase(sessionResult)

        assertEquals(0.3f, fakeRepository.familiarityUpdates[2] ?: 0f, 0.01f)
    }

    @Test
    fun `word is marked as learned when new score reaches 0_8 or higher`() = runTest {
        val word = createWord(id = 3, familiarity = 0.75, isLearned = false)
        val sessionResult = SessionResult(
            wordResults = listOf(WordQuizResult(word = word, wasCorrect = true)), // 0.75 + 0.1 = 0.85 >= 0.8
            totalCorrect = 1,
            totalMistakes = 0,
            grade = "A",
            gradeProgress = 1f,
            congratsTitle = "Mastered",
            congratsMessage = "Word learned"
        )

        useCase(sessionResult)

        assertTrue(fakeRepository.learnedStatusUpdates[3] == true)
    }

    @Test
    fun `review date interval schedules longer review for higher score`() = runTest {
        val wordMastered = createWord(id = 4, familiarity = 0.85) // will become 0.95 -> 30 days
        val wordStruggling = createWord(id = 5, familiarity = 0.2) // will become 0.0 -> 1 day

        val sessionResult = SessionResult(
            wordResults = listOf(
                WordQuizResult(word = wordMastered, wasCorrect = true),
                WordQuizResult(word = wordStruggling, wasCorrect = false)
            ),
            totalCorrect = 1,
            totalMistakes = 1,
            grade = "C",
            gradeProgress = 0.5f,
            congratsTitle = "Done",
            congratsMessage = "Keep going"
        )

        useCase(sessionResult)

        val now = System.currentTimeMillis()
        val masteredReview = fakeRepository.nextReviewDateUpdates[4] ?: 0L
        val strugglingReview = fakeRepository.nextReviewDateUpdates[5] ?: 0L

        // Mastered should be scheduled far in advance (~30 days) vs Struggling (~1 day)
        assertTrue(masteredReview > strugglingReview)
        assertTrue(masteredReview >= now + (29 * 24 * 60 * 60 * 1000L))
    }

    private class FakeLearningRepository : LearningRepository {
        val familiarityUpdates = mutableMapOf<Int, Float>()
        val nextReviewDateUpdates = mutableMapOf<Int, Long>()
        val learnedStatusUpdates = mutableMapOf<Int, Boolean>()

        override suspend fun updateFamiliarityScore(word: VocabularyWord, score: Float) {
            familiarityUpdates[word.id] = score
        }

        override suspend fun updateNextReviewDate(word: VocabularyWord, nextReviewDate: Long) {
            nextReviewDateUpdates[word.id] = nextReviewDate
        }

        override suspend fun updateLearnedStatus(word: VocabularyWord, newStatus: Boolean) {
            learnedStatusUpdates[word.id] = newStatus
        }

        override suspend fun getFavLearnedState(userName: String): FavLearnedState = FavLearnedState()
        override suspend fun fetchSessionWords(level: String, wordsPerSession: Int): List<VocabularyWord> = emptyList()
        override suspend fun getAllUnlearnedWords(level: String): List<VocabularyWord> = emptyList()
        override suspend fun updateLearnedStatus(words: List<VocabularyWord>) {}
        override suspend fun updateJustLearnedStatus(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int) {}
        override suspend fun updateFavoriteStatus(word: VocabularyWord, newStatus: Boolean) {}
        override suspend fun getJustLearnedSessionData(level: String): JustLearnedSessionData = JustLearnedSessionData(emptyList(), null)
        override suspend fun recordCorrectAnswer(word: VocabularyWord) {}
        override suspend fun recordMistake(word: VocabularyWord) {}
        override suspend fun skipWord(word: VocabularyWord) {}
        override suspend fun updateLastSeen(word: VocabularyWord) {}
    }
}
