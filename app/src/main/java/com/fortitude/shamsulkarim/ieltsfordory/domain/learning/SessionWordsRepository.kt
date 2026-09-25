package com.fortitude.shamsulkarim.ieltsfordory.domain.learning

import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Repository for storing and retrieving session words between screens.
 * 
 * Flow: HomeScreen stores words → SessionScreen reads them
 * 
 * This is a singleton that holds the selected words for the current learning session.
 */
class SessionWordsRepository {
    
    private var _sessionWords: List<VocabularyWord> = emptyList()
    
    // Flag to signal HomeScreen should refresh words (set after completing a session)
    private var _shouldRefreshHome: Boolean = false
    
    private val _refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshTrigger: SharedFlow<Unit> = _refreshTrigger.asSharedFlow()
    
    /**
     * Store words selected for the learning session.
     * Called by HomeViewModel when user clicks "Start".
     */
    fun setSessionWords(words: List<VocabularyWord>) {
        _sessionWords = words
    }
    
    /**
     * Get the words for the current session.
     * Called by SessionViewModel to load session data.
     */
    fun getSessionWords(): List<VocabularyWord> = _sessionWords
    
    /**
     * Check if there are words available for a session.
     */
    fun hasSessionWords(): Boolean = _sessionWords.isNotEmpty()
    
    /**
     * Clear the session words (call after session completes).
     */
    fun clearSession() {
        _sessionWords = emptyList()
    }
    
    /**
     * Signal that HomeScreen should refresh words on next visit.
     * Called from ResultScreen when user finishes a session.
     */
    fun requestHomeRefresh() {
        _shouldRefreshHome = true
        _refreshTrigger.tryEmit(Unit)
    }
    
    /**
     * Check and consume the refresh flag.
     * Returns true if refresh was requested, then resets the flag.
     * Called by HomeScreen on resume.
     */
    fun consumeRefreshRequest(): Boolean {
        val shouldRefresh = _shouldRefreshHome
        _shouldRefreshHome = false
        return shouldRefresh
    }
}

