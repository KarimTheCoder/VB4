package com.fortitude.shamsulkarim.ieltsfordory.data.sync

import android.content.Context
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.DatabaseRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.testutil.FakeSharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseSyncManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var mockContext: Context
    private lateinit var fakeDbRepo: FakeDatabaseRepo
    private lateinit var fakeVocabRepo: FakeVocabularyRepo
    private lateinit var fakeSharedPreferences: FakeSharedPreferences
    private lateinit var syncManager: FirebaseSyncManager

    private class FakeDatabaseRepo : DatabaseRepository {
        var capturedListener: ChildEventListener? = null
        var capturedUserId: String? = null

        override fun updateUserData(userId: String, data: Any, listener: OnCompleteListener<Void>?) {}
        override fun addChildEventListener(userId: String, listener: ChildEventListener) {
            capturedUserId = userId
            capturedListener = listener
        }
        override fun removeChildEventListener(userId: String, listener: ChildEventListener) {
            if (capturedListener == listener) capturedListener = null
        }
    }

    private class FakeVocabularyRepo : VocabularyRepository {
        val favCalls = mutableListOf<Triple<VocabularySource, Int, Boolean>>()
        val learnedCalls = mutableListOf<Triple<VocabularySource, Int, Boolean>>()

        override suspend fun updateFavorite(source: VocabularySource, wordId: Int, isFavorite: Boolean) {
            favCalls.add(Triple(source, wordId, isFavorite))
        }

        override suspend fun updateLearnState(source: VocabularySource, wordId: Int, isLearned: Boolean) {
            learnedCalls.add(Triple(source, wordId, isLearned))
        }

        override suspend fun getVocabulary(level: String): List<VocabularyWord> = emptyList()
        override suspend fun getFavoriteWords(): List<VocabularyWord> = emptyList()
        override suspend fun getLearnedWords(level: String): List<VocabularyWord> = emptyList()
        override suspend fun getUnlearnedWords(level: String): List<VocabularyWord> = emptyList()
        override suspend fun getAllUnlearnedWords(): List<VocabularyWord> = emptyList()
        override suspend fun getLearnedCount(level: String): Int = 0
        override suspend fun getTotalCount(level: String): Int = 0
        override suspend fun getRandomWords(limit: Int, excludeIds: Set<Int>): List<VocabularyWord> = emptyList()
    }

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        fakeDbRepo = FakeDatabaseRepo()
        fakeVocabRepo = FakeVocabularyRepo()
        fakeSharedPreferences = FakeSharedPreferences()

        whenever(mockContext.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE))
            .thenReturn(fakeSharedPreferences)

        syncManager = FirebaseSyncManager(
            mockContext,
            AddChildEventListenerUseCase(fakeDbRepo),
            UpdateFavoriteStateUseCase(fakeVocabRepo),
            UpdateLearnStateUseCase(fakeVocabRepo),
            testScope
        )
    }

    @Test
    fun `builderToNums parses binary bitstring correctly`() {
        val bitstring = "1+0+1+1+0+"
        val nums = syncManager.builderToNums(bitstring)
        assertEquals(listOf(1, 0, 1, 1, 0), nums)
    }

    @Test
    fun `parseData extracts modern keys and updates username`() {
        val modernData = mapOf(
            "name" to "Alice",
            "ieltsFavCount" to "1+0+",
            "toeflFavCount" to "0+1+",
            "satFavCount" to "1+1+",
            "greFavCount" to "0+0+",
            "ieltsLearnedCount" to "1+1+",
            "toeflLearnedCount" to "0+0+",
            "satLearnedCount" to "1+0+",
            "greLearnedCount" to "0+1+"
        )

        syncManager.parseData(modernData)

        assertEquals("Alice", fakeSharedPreferences.getString(AppPreferences.KEY_USER_NAME, null))
    }

    @Test
    fun `parseData supports legacy keys backwards-compatibly`() {
        val legacyData = mapOf(
            "name" to "Bob",
            "beginnerFavCount" to "1+0+",
            "intermediateFavCount" to "0+1+",
            "advanceFavCount" to "1+1+",
            "greFavCount" to "0+0+",
            "beginnerLearnedCount" to "1+1+",
            "intermediateLearnedCount" to "0+0+",
            "advanceLearnedCount" to "1+0+",
            "greLearnedCount" to "0+1+"
        )

        syncManager.parseData(legacyData)

        assertEquals("Bob", fakeSharedPreferences.getString(AppPreferences.KEY_USER_NAME, null))
    }

    @Test
    fun `onChildChanged handles modern ieltsFavCount with 0-based word IDs`() = testScope.runTest {
        syncManager.startSync("user123", null)
        val listener = fakeDbRepo.capturedListener!!

        val snapshot = mock(DataSnapshot::class.java)
        whenever(snapshot.key).thenReturn("ieltsFavCount")
        whenever(snapshot.getValue(String::class.java)).thenReturn("1+0+1+")

        listener.onChildChanged(snapshot, null)
        advanceUntilIdle()

        // 0-based indices: wordId 0 is true, wordId 1 is false, wordId 2 is true
        assertEquals(3, fakeVocabRepo.favCalls.size)
        assertEquals(Triple(VocabularySource.IELTS, 0, true), fakeVocabRepo.favCalls[0])
        assertEquals(Triple(VocabularySource.IELTS, 1, false), fakeVocabRepo.favCalls[1])
        assertEquals(Triple(VocabularySource.IELTS, 2, true), fakeVocabRepo.favCalls[2])
    }

    @Test
    fun `onChildChanged handles modern greLearnedCount with 0-based word IDs`() = testScope.runTest {
        syncManager.startSync("user123", null)
        val listener = fakeDbRepo.capturedListener!!

        val snapshot = mock(DataSnapshot::class.java)
        whenever(snapshot.key).thenReturn("greLearnedCount")
        whenever(snapshot.getValue(String::class.java)).thenReturn("0+1+")

        listener.onChildChanged(snapshot, null)
        advanceUntilIdle()

        assertEquals(2, fakeVocabRepo.learnedCalls.size)
        assertEquals(Triple(VocabularySource.GRE, 0, false), fakeVocabRepo.learnedCalls[0])
        assertEquals(Triple(VocabularySource.GRE, 1, true), fakeVocabRepo.learnedCalls[1])
    }

    @Test
    fun `onChildChanged handles legacy advanceFavCount correctly`() = testScope.runTest {
        syncManager.startSync("user123", null)
        val listener = fakeDbRepo.capturedListener!!

        val snapshot = mock(DataSnapshot::class.java)
        whenever(snapshot.key).thenReturn("advanceFavCount")
        whenever(snapshot.getValue(String::class.java)).thenReturn("1+")

        listener.onChildChanged(snapshot, null)
        advanceUntilIdle()

        assertEquals(1, fakeVocabRepo.favCalls.size)
        assertEquals(Triple(VocabularySource.SAT, 0, true), fakeVocabRepo.favCalls[0])
    }

    @Test
    fun `initial sync triggers automatically when callback is null`() = testScope.runTest {
        syncManager.startSync("user123", null)
        val listener = fakeDbRepo.capturedListener!!

        val keys = listOf(
            "name" to "User",
            "ieltsFavCount" to "1+",
            "toeflFavCount" to "0+",
            "satFavCount" to "1+",
            "greFavCount" to "0+",
            "ieltsLearnedCount" to "1+",
            "toeflLearnedCount" to "0+",
            "satLearnedCount" to "1+",
            "greLearnedCount" to "0+"
        )

        keys.forEach { (key, value) ->
            val snapshot = mock(DataSnapshot::class.java)
            whenever(snapshot.key).thenReturn(key)
            whenever(snapshot.getValue(String::class.java)).thenReturn(value)
            whenever(snapshot.exists()).thenReturn(true)
            listener.onChildAdded(snapshot, null)
        }

        advanceUntilIdle()

        // Verify initial sync was performed
        assertTrue(fakeVocabRepo.favCalls.contains(Triple(VocabularySource.IELTS, 0, true)))
        assertTrue(fakeVocabRepo.favCalls.contains(Triple(VocabularySource.TOEFL, 0, false)))
        assertTrue(fakeVocabRepo.favCalls.contains(Triple(VocabularySource.SAT, 0, true)))
        assertTrue(fakeVocabRepo.favCalls.contains(Triple(VocabularySource.GRE, 0, false)))

        assertTrue(fakeVocabRepo.learnedCalls.contains(Triple(VocabularySource.IELTS, 0, true)))
        assertTrue(fakeVocabRepo.learnedCalls.contains(Triple(VocabularySource.TOEFL, 0, false)))
        assertTrue(fakeVocabRepo.learnedCalls.contains(Triple(VocabularySource.SAT, 0, true)))
        assertTrue(fakeVocabRepo.learnedCalls.contains(Triple(VocabularySource.GRE, 0, false)))
    }
}
