package com.fortitude.shamsulkarim.ieltsfordory.data.learning.room

import android.content.Context
import android.content.res.Resources
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.SessionWordDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class RoomLearningRepositoryTest {

    private lateinit var mockWordProgressDao: WordProgressDao
    private lateinit var mockSessionWordDao: SessionWordDao
    private lateinit var mockVocabularyRepository: VocabularyRepository
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var repository: RoomLearningRepository

    @Before
    fun setUp() {
        mockWordProgressDao = mock(WordProgressDao::class.java)
        mockSessionWordDao = mock(SessionWordDao::class.java)
        mockVocabularyRepository = mock(VocabularyRepository::class.java)
        mockContext = mock(Context::class.java)
        mockResources = mock(Resources::class.java)

        whenever(mockContext.resources).thenReturn(mockResources)

        // Mock 3 words per source
        val threeWords = arrayOf("word0", "word1", "word2")
        whenever(mockResources.getStringArray(R.array.IELTS_words)).thenReturn(threeWords)
        whenever(mockResources.getStringArray(R.array.TOEFL_words)).thenReturn(threeWords)
        whenever(mockResources.getStringArray(R.array.SAT_words)).thenReturn(threeWords)
        whenever(mockResources.getStringArray(R.array.GRE_words)).thenReturn(threeWords)

        repository = RoomLearningRepository(
            mockWordProgressDao,
            mockSessionWordDao,
            mockVocabularyRepository,
            mockContext
        )
    }

    @Test
    fun `getFavLearnedState builds exact bitstring based on room entities`() = runTest {
        // IELTS: word 0 is favorite, word 2 is learned
        val ieltsEntities = listOf(
            WordProgressEntity(wordId = 0, source = "IELTS", isFavorite = true, isLearned = false),
            WordProgressEntity(wordId = 2, source = "IELTS", isFavorite = false, isLearned = true)
        )
        whenever(mockWordProgressDao.getBySource("IELTS")).thenReturn(ieltsEntities)
        whenever(mockWordProgressDao.getBySource("TOEFL")).thenReturn(emptyList())
        whenever(mockWordProgressDao.getBySource("SAT")).thenReturn(emptyList())
        whenever(mockWordProgressDao.getBySource("GRE")).thenReturn(emptyList())

        val state = repository.getFavLearnedState("TestUser")

        assertEquals("TestUser", state.name)
        // IELTS: word 0=1, word 1=0, word 2=0 for fav -> "1+0+0+"
        assertEquals("1+0+0+", state.ieltsFavCount)
        // IELTS: word 0=0, word 1=0, word 2=1 for learned -> "0+0+1+"
        assertEquals("0+0+1+", state.ieltsLearnedCount)
        // TOEFL: all 0
        assertEquals("0+0+0+", state.toeflFavCount)
        assertEquals("0+0+0+", state.toeflLearnedCount)
    }
}
