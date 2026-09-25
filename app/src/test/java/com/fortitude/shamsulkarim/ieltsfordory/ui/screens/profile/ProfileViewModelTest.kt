package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserPreferencesRepository: UserPreferencesRepository
    private lateinit var mockContext: Context
    private lateinit var mockVocabularyRepository: VocabularyRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockUserPreferencesRepository = mock(UserPreferencesRepository::class.java)
        mockContext = mock(Context::class.java)
        mockVocabularyRepository = mock(VocabularyRepository::class.java)

        val mockAppInfo = mock(ApplicationInfo::class.java)
        val mockPackageManager = mock(PackageManager::class.java)
        whenever(mockContext.applicationInfo).thenReturn(mockAppInfo)
        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockAppInfo.loadLabel(mockPackageManager)).thenReturn("Dory")

        whenever(mockUserPreferencesRepository.reminderStatusFlow).thenReturn(flowOf(false))
        whenever(mockUserPreferencesRepository.reminderHourFlow).thenReturn(flowOf(20))
        whenever(mockUserPreferencesRepository.reminderMinuteFlow).thenReturn(flowOf(0))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshStats updates learning statistics from vocabulary repository`() = runTest {
        whenever(mockVocabularyRepository.getTotalCount("beginner")).thenReturn(100)
        whenever(mockVocabularyRepository.getLearnedCount("beginner")).thenReturn(30)
        whenever(mockVocabularyRepository.getTotalCount("intermediate")).thenReturn(100)
        whenever(mockVocabularyRepository.getLearnedCount("intermediate")).thenReturn(20)
        whenever(mockVocabularyRepository.getTotalCount("advanced")).thenReturn(100)
        whenever(mockVocabularyRepository.getLearnedCount("advanced")).thenReturn(10)

        viewModel = ProfileViewModel(
            mockUserPreferencesRepository,
            mockContext,
            mockVocabularyRepository
        )
        advanceUntilIdle()

        // 300 total, 60 learned, 240 left
        assertEquals(300, viewModel.uiState.value.totalWords)
        assertEquals(60, viewModel.uiState.value.learnedWords)
        assertEquals(240, viewModel.uiState.value.wordsLeftToLearn)

        // Simulate learning more words in a session
        whenever(mockVocabularyRepository.getLearnedCount("beginner")).thenReturn(40)

        // Call refreshStats
        viewModel.refreshStats()
        advanceUntilIdle()

        // 300 total, 70 learned, 230 left
        assertEquals(70, viewModel.uiState.value.learnedWords)
        assertEquals(230, viewModel.uiState.value.wordsLeftToLearn)
    }
}
