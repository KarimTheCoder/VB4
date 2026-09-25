package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words

import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadAudioUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.IsTtsReadyUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.SpeakTextUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetFavoriteWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetVocabularyUseCase
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
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UnifiedWordsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockGetVocabularyUseCase: GetVocabularyUseCase
    private lateinit var mockGetLearnedWordsUseCase: GetLearnedWordsUseCase
    private lateinit var mockGetFavoriteWordsUseCase: GetFavoriteWordsUseCase
    private lateinit var mockUpdateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
    private lateinit var mockDownloadAudioUseCase: DownloadAudioUseCase
    private lateinit var mockIsConnectedUseCase: IsConnectedUseCase
    private lateinit var mockIsTtsReadyUseCase: IsTtsReadyUseCase
    private lateinit var mockSpeakTextUseCase: SpeakTextUseCase
    private lateinit var mockAppPreferences: AppPreferences

    private lateinit var viewModel: UnifiedWordsViewModel

    private val sampleWord1 = VocabularyWord(
        id = 0,
        word = "Abate",
        translation = "To decrease",
        source = VocabularySource.IELTS,
        level = "beginner"
    )

    private val sampleWord2 = VocabularyWord(
        id = 1,
        word = "Benevolent",
        translation = "Kind",
        source = VocabularySource.IELTS,
        level = "beginner",
        isFavorite = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockGetVocabularyUseCase = mock(GetVocabularyUseCase::class.java)
        mockGetLearnedWordsUseCase = mock(GetLearnedWordsUseCase::class.java)
        mockGetFavoriteWordsUseCase = mock(GetFavoriteWordsUseCase::class.java)
        mockUpdateFavoriteStatusUseCase = mock(UpdateFavoriteStatusUseCase::class.java)
        mockDownloadAudioUseCase = mock(DownloadAudioUseCase::class.java)
        mockIsConnectedUseCase = mock(IsConnectedUseCase::class.java)
        mockIsTtsReadyUseCase = mock(IsTtsReadyUseCase::class.java)
        mockSpeakTextUseCase = mock(SpeakTextUseCase::class.java)
        mockAppPreferences = mock(AppPreferences::class.java)

        whenever(mockAppPreferences.getPrevWordSelection()).thenReturn(0)
        whenever(mockAppPreferences.isIELTSActive).thenReturn(true)
        whenever(mockAppPreferences.isTOEFLActive).thenReturn(true)
        whenever(mockAppPreferences.isSATActive).thenReturn(true)
        whenever(mockAppPreferences.isGREActive).thenReturn(true)
        whenever(mockAppPreferences.secondLanguage).thenReturn("english")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load fetches vocabulary and populates UI state`() = runTest {
        whenever(mockGetVocabularyUseCase.execute("beginner")).thenReturn(listOf(sampleWord1, sampleWord2))
        whenever(mockGetLearnedWordsUseCase.execute("beginner")).thenReturn(emptyList())
        whenever(mockGetFavoriteWordsUseCase.execute()).thenReturn(listOf(sampleWord2))

        viewModel = UnifiedWordsViewModel(
            mockGetVocabularyUseCase,
            mockGetLearnedWordsUseCase,
            mockGetFavoriteWordsUseCase,
            mockUpdateFavoriteStatusUseCase,
            mockDownloadAudioUseCase,
            mockIsConnectedUseCase,
            mockIsTtsReadyUseCase,
            mockSpeakTextUseCase,
            mockAppPreferences
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.allWords.size)
        assertEquals(1, state.favoriteWords.size)
    }

    @Test
    fun `refreshIfNeeded reloads data and preserves active search filter`() = runTest {
        whenever(mockGetVocabularyUseCase.execute("beginner")).thenReturn(listOf(sampleWord1, sampleWord2))
        whenever(mockGetLearnedWordsUseCase.execute("beginner")).thenReturn(emptyList())
        whenever(mockGetFavoriteWordsUseCase.execute()).thenReturn(listOf(sampleWord2))

        viewModel = UnifiedWordsViewModel(
            mockGetVocabularyUseCase,
            mockGetLearnedWordsUseCase,
            mockGetFavoriteWordsUseCase,
            mockUpdateFavoriteStatusUseCase,
            mockDownloadAudioUseCase,
            mockIsConnectedUseCase,
            mockIsTtsReadyUseCase,
            mockSpeakTextUseCase,
            mockAppPreferences
        )
        advanceUntilIdle()

        // Set search query
        viewModel.updateSearchQuery("Bene")
        assertEquals(1, viewModel.uiState.value.filteredAllWords.size)
        assertEquals("Benevolent", viewModel.uiState.value.filteredAllWords.first().word)

        // Now simulate new words learned in DB
        val sampleWord3 = VocabularyWord(
            id = 2,
            word = "Benefactor",
            translation = "Supporter",
            source = VocabularySource.IELTS,
            level = "beginner"
        )
        whenever(mockGetVocabularyUseCase.execute("beginner")).thenReturn(listOf(sampleWord1, sampleWord2, sampleWord3))

        // Trigger refresh
        viewModel.refreshIfNeeded()
        advanceUntilIdle()

        // Search query "Bene" should filter across the newly loaded words
        assertEquals(3, viewModel.uiState.value.allWords.size)
        assertEquals(2, viewModel.uiState.value.filteredAllWords.size)
        assertTrue(viewModel.uiState.value.filteredAllWords.any { it.word == "Benevolent" })
        assertTrue(viewModel.uiState.value.filteredAllWords.any { it.word == "Benefactor" })
    }
}
