package com.fortitude.shamsulkarim.ieltsfordory.data.vocabulary.room

import android.content.Context
import android.content.res.Resources
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.testutil.FakeSharedPreferences
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.Mockito.mock

class RoomVocabularyRepositoryTest {

    private lateinit var mockDao: WordProgressDao
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var sharedPreferences: FakeSharedPreferences
    private lateinit var repository: RoomVocabularyRepository

    @Before
    fun setUp() = runTest {
        mockDao = mock(WordProgressDao::class.java)
        mockContext = mock(Context::class.java)
        mockResources = mock(Resources::class.java)
        sharedPreferences = FakeSharedPreferences()

        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockContext.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)
        whenever(mockDao.getBySource(any())).thenReturn(emptyList())
        whenever(mockDao.getFavorites(any())).thenReturn(emptyList())
        whenever(mockDao.getLearned(any())).thenReturn(emptyList())

        val dummyWords = arrayOf("word1", "word2", "word3", "word4", "word5", "word6", "word7", "word8", "word9", "word10")
        val dummyTranslations = arrayOf("trans1", "trans2", "trans3", "trans4", "trans5", "trans6", "trans7", "trans8", "trans9", "trans10")
        val dummyGrammar = arrayOf("n.", "v.", "adj.", "n.", "v.", "adj.", "n.", "v.", "adj.", "n.")
        val dummyPronun = arrayOf("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10")
        val dummyExamples = arrayOf("ex1", "ex2", "ex3", "ex4", "ex5", "ex6", "ex7", "ex8", "ex9", "ex10")
        val dummyLevels = arrayOf("beginner", "beginner", "beginner", "intermediate", "intermediate", "intermediate", "intermediate", "advanced", "advanced", "advanced")
        val dummyPositions = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val dummySpanishWords = arrayOf("palabra1", "palabra2", "palabra3", "palabra4", "palabra5", "palabra6", "palabra7", "palabra8", "palabra9", "palabra10")
        val dummySpanishTrans = arrayOf("trad1", "trad2", "trad3", "trad4", "trad5", "trad6", "trad7", "trad8", "trad9", "trad10")

        whenever(mockResources.getStringArray(any())).thenReturn(dummyWords)
        whenever(mockResources.getIntArray(any())).thenReturn(dummyPositions)

        repository = RoomVocabularyRepository(mockDao, mockContext)
    }

    @Test
    fun `when category is disabled in preferences it is filtered out dynamically`() = runTest {
        // By default, all categories are active
        val allWords = repository.getVocabulary("beginner")
        val sources = allWords.map { it.source }.toSet()
        assertTrue(sources.contains(VocabularySource.IELTS))
        assertTrue(sources.contains(VocabularySource.TOEFL))

        // Disable TOEFL in preferences
        sharedPreferences.edit().putBoolean("isTOEFLActive", false).apply()

        // Query again - TOEFL should be filtered out dynamically
        val filteredWords = repository.getVocabulary("beginner")
        val filteredSources = filteredWords.map { it.source }.toSet()
        assertTrue(filteredSources.contains(VocabularySource.IELTS))
        assertFalse(filteredSources.contains(VocabularySource.TOEFL))
    }

    @Test
    fun `getFavoriteWords filters out disabled categories`() = runTest {
        val ieltsFav = WordProgressEntity(wordId = 0, source = "IELTS", isFavorite = true)
        val toeflFav = WordProgressEntity(wordId = 0, source = "TOEFL", isFavorite = true)

        whenever(mockDao.getFavorites("IELTS")).thenReturn(listOf(ieltsFav))
        whenever(mockDao.getFavorites("TOEFL")).thenReturn(listOf(toeflFav))

        // Both active
        val favsAll = repository.getFavoriteWords()
        assertEquals(2, favsAll.size)

        // Disable IELTS
        sharedPreferences.edit().putBoolean("isIELTSActive", false).apply()
        val favsFiltered = repository.getFavoriteWords()
        assertEquals(1, favsFiltered.size)
        assertEquals(VocabularySource.TOEFL, favsFiltered.first().source)
    }

    @Test
    fun `secondLanguage populates Spanish translations when not english`() = runTest {
        // Default language is english
        sharedPreferences.edit().putString("secondlanguage", "english").apply()
        val englishWords = repository.getVocabulary("beginner")
        assertNull(englishWords.first().translationSecondLang)

        // Switch to Spanish
        sharedPreferences.edit().putString("secondlanguage", "Spanish").apply()
        val spanishWords = repository.getVocabulary("beginner")
        assertNotNull(spanishWords.first().translationSecondLang)
    }
}
