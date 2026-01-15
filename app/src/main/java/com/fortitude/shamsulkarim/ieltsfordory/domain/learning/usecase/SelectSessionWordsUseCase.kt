package com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase

import android.util.Log
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.WordSelectionConfig
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord

/**
 * Use case for selecting words for a learning session.
 * 
 * Algorithm Priority:
 * 1. New words (never seen) - highest priority
 * 2. Most mistaken words - high priority  
 * 3. Words with low familiarity score - medium priority
 * 4. Words not seen recently - lower priority
 * 
 * Respects skip list from HomeScreen and excludes learned/blacklisted words.
 */
class SelectSessionWordsUseCase(
    private val vocabularyRepository: VocabularyRepository,
    private val wordProgressDao: WordProgressDao
) {
    companion object {
        private const val TAG = "SelectWords"
    }

    /**
     * Selects words for a learning session based on the provided configuration.
     * 
     * @param config Configuration specifying level, count, and exclusions
     * @return List of selected vocabulary words, prioritized by the algorithm
     */
    suspend operator fun invoke(config: WordSelectionConfig): List<VocabularyWord> {
        Log.d(TAG, "Starting word selection: requested=${config.wordsPerSession}, skipped=${config.skipWordIds.size}")

        // Step 1: Get ALL unlearned words from all sources (no level filtering)
        val unlearnedWords = vocabularyRepository.getAllUnlearnedWords()
        Log.d(TAG, "Found ${unlearnedWords.size} total unlearned words")

        if (unlearnedWords.isEmpty()) {
            Log.w(TAG, "No unlearned words available")
            return emptyList()
        }

        // Step 2: Filter out skipped words
        val availableWords = unlearnedWords.filter { word ->
            word.id !in config.skipWordIds
        }
        Log.d(TAG, "After filtering skipped: ${availableWords.size} words available")

        if (availableWords.isEmpty()) {
            Log.w(TAG, "All words have been skipped for level: ${config.level}")
            return emptyList()
        }

        // Step 3: Score and prioritize each word
        val scoredWords = availableWords.map { word ->
            val progress = wordProgressDao.getBySourceAndWordId(word.source.name, word.id)
            val score = calculatePriorityScore(word, progress)
            ScoredWord(word, score)
        }

        // Step 4: Sort by score (highest first) and take requested count
        val selectedWords = scoredWords
            .sortedByDescending { it.score }
            .take(config.wordsPerSession)
            .map { it.word }

        Log.i(TAG, "Selected ${selectedWords.size} words: ${selectedWords.map { "'${it.word}'" }}")

        // Log individual scores for debugging
        scoredWords.take(config.wordsPerSession).forEach { scored ->
            Log.d(TAG, "  - '${scored.word.word}' score=${scored.score}")
        }

        return selectedWords
    }

    /**
     * Calculates a priority score for a word.
     * Higher score = higher priority for selection.
     */
    private fun calculatePriorityScore(word: VocabularyWord, progress: WordProgressEntity?): Float {
        // New word (no progress record) gets maximum priority
        if (progress == null) {
            Log.d(TAG, "Word '${word.word}' has no progress - assigning max priority (100)")
            return 100f
        }

        var score = 50f  // Base score for words with progress

        // Factor 1: Mistake count (more mistakes = higher priority)
        val mistakeBonus = progress.mistakeCount * 10f
        score += mistakeBonus

        // Factor 2: Correct count (more correct = lower priority)
        val correctPenalty = progress.correctCount * 2f
        score -= correctPenalty

        // Factor 3: Familiarity score (higher familiarity = lower priority)
        val familiarityPenalty = progress.familiarityScore * 20f
        score -= familiarityPenalty

        // Factor 4: Time since last seen (longer = higher priority)
        progress.lastSeenDate?.let { lastSeen ->
            val daysSinceLastSeen = (System.currentTimeMillis() - lastSeen) / (1000 * 60 * 60 * 24)
            val timeBonus = daysSinceLastSeen.coerceAtMost(10) * 2f  // Max 20 points
            score += timeBonus
        }

        // Factor 5: Spaced Repetition (Next Review Date)
        progress.nextReviewDate?.let { nextReview ->
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextReview) {
                // DUE: High priority bonus
                val daysOverdue = (currentTime - nextReview) / (1000 * 60 * 60 * 24)
                score += 80f + (daysOverdue * 5f) // Big bonus to prioritize due words over new words (which are 100)
                Log.d(TAG, "Word due for review (+${80 + daysOverdue * 5})")
            } else {
                // NOT DUE: Penalty
                score -= 200f // Strong penalty to avoid reviewing too soon
                Log.d(TAG, "Word not due yet (penalty -200)")
            }
        }

        val finalScore = score.coerceAtLeast(0f)

        Log.d(TAG, "Score for '${word.word}': base=50, mistakes=+${mistakeBonus.toInt()}, " +
                "correct=-${correctPenalty.toInt()}, familiarity=-${familiarityPenalty.toInt()} = $finalScore")

        return finalScore
    }
}

/**
 * Internal data class for scoring words during selection.
 */
private data class ScoredWord(
    val word: VocabularyWord,
    val score: Float
)
