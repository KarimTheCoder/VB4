package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.app.Activity
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.sync.FirebaseSyncManager
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.SessionWordsRepository
import com.fortitude.shamsulkarim.ieltsfordory.testutil.FakeSharedPreferences
import com.fortitude.shamsulkarim.ieltsfordory.testutil.TestContextFactory
import com.google.firebase.auth.FirebaseUser
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
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsComposeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSp: FakeSharedPreferences
    private lateinit var appPreferences: AppPreferences
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeUserPrefsRepo: FakeUserPreferencesRepository
    private lateinit var fakeSyncManager: FakeSyncManager
    private lateinit var sessionWordsRepository: SessionWordsRepository
    private lateinit var viewModel: SettingsComposeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSp = FakeSharedPreferences()
        val context = TestContextFactory.createContext(fakeSp)
        appPreferences = AppPreferences.get(context)
        fakeAuthRepo = FakeAuthRepository()
        fakeUserPrefsRepo = FakeUserPreferencesRepository(context)
        fakeSyncManager = FakeSyncManager()
        sessionWordsRepository = SessionWordsRepository()

        viewModel = SettingsComposeViewModel(
            appPreferences = appPreferences,
            authRepository = fakeAuthRepo,
            userPreferencesRepository = fakeUserPrefsRepo,
            syncManager = fakeSyncManager,
            sessionWordsRepository = sessionWordsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setDarkMode updates UI state and dirty flag, and persists on saveSettings`() = runTest {
        viewModel.setDarkMode(1) // Dark mode
        advanceUntilIdle()

        // Draft state updated, unsaved changes true, preferences not yet updated
        assertEquals(1, viewModel.uiState.value.darkModeIndex)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertEquals(0, appPreferences.getDarkMode())

        // Save settings commits changes
        var onSavedCalled = false
        viewModel.saveSettings { onSavedCalled = true }
        advanceUntilIdle()

        assertTrue(onSavedCalled)
        assertEquals(1, appPreferences.getDarkMode())
        assertEquals(1, fakeUserPrefsRepo.lastDarkMode)
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
        assertEquals("Settings saved", viewModel.uiState.value.toastMessage)
    }

    @Test
    fun `setSound updates UI state as draft and reverts dirty flag when restored`() {
        viewModel.setSound(false)
        assertFalse(viewModel.uiState.value.soundEnabled)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertTrue(appPreferences.soundState) // Persisted value unchanged before save

        // Reverting back to saved value clears dirty flag
        viewModel.setSound(true)
        assertTrue(viewModel.uiState.value.soundEnabled)
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)

        // Changing again and saving commits to preferences
        viewModel.setSound(false)
        viewModel.saveSettings()
        assertFalse(appPreferences.soundState)
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `setPronunciation updates pronunState in UI state as draft and persists on save`() {
        viewModel.setPronunciation(false)
        assertFalse(viewModel.uiState.value.pronunciationEnabled)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertTrue(appPreferences.pronunState)

        viewModel.saveSettings()
        assertFalse(appPreferences.pronunState)
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `setWordsPerSession updates draft and persists on saveSettings`() = runTest {
        viewModel.setWordsPerSession(2) // Position 2 corresponds to 15 words
        advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.wordsPerSession)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertEquals(5, appPreferences.wordsPerSession) // Default 5 before save

        viewModel.saveSettings()
        advanceUntilIdle()

        assertEquals(15, appPreferences.wordsPerSession)
        assertEquals(15, fakeUserPrefsRepo.lastWordsPerSession)
        assertTrue(sessionWordsRepository.consumeRefreshRequest())
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `setRepetitionsPerSession updates draft and persists on saveSettings`() = runTest {
        viewModel.setRepetitionsPerSession(3) // Position 3 corresponds to 10 repetitions
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.repetitionsPerSession)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertEquals(5, appPreferences.repeatationPerSession) // Default 5 before save

        viewModel.saveSettings()
        advanceUntilIdle()

        assertEquals(10, appPreferences.repeatationPerSession)
        assertEquals(10, fakeUserPrefsRepo.lastRepetitionPerSession)
        assertTrue(sessionWordsRepository.consumeRefreshRequest())
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `category filters update draft, enforce at least one category, and persist on save`() {
        viewModel.setIeltsActive(false)
        assertFalse(viewModel.uiState.value.ieltsActive)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertTrue(appPreferences.isIELTSActive) // Not yet saved

        viewModel.setToeflActive(false)
        assertFalse(viewModel.uiState.value.toeflActive)

        viewModel.setSatActive(false)
        assertFalse(viewModel.uiState.value.satActive)

        // Attempting to deactivate GRE (last remaining category) must be rejected
        viewModel.setGreActive(false)
        assertTrue(viewModel.uiState.value.greActive)
        assertEquals("At least select one", viewModel.uiState.value.errorMessage)

        viewModel.saveSettings()
        assertFalse(appPreferences.isIELTSActive)
        assertFalse(appPreferences.isTOEFLActive)
        assertFalse(appPreferences.isSATActive)
        assertTrue(appPreferences.isGREActive)
        assertTrue(sessionWordsRepository.consumeRefreshRequest())
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
    }

    @Test
    fun `toggleSpanish updates secondlanguage draft and persists on save`() {
        assertFalse(viewModel.uiState.value.isSpanishEnabled)

        viewModel.toggleSpanish()
        assertTrue(viewModel.uiState.value.isSpanishEnabled)
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertTrue(appPreferences.secondLanguage.equals("english", ignoreCase = true)) // Not yet saved

        viewModel.saveSettings()
        assertEquals("spanish", appPreferences.secondLanguage)
        assertFalse(viewModel.uiState.value.hasUnsavedChanges)

        viewModel.toggleSpanish()
        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        viewModel.saveSettings()
        assertEquals("english", appPreferences.secondLanguage)
    }

    @Test
    fun `discardChanges resets draft state back to saved snapshot`() {
        viewModel.setSound(false)
        viewModel.setWordsPerSession(2)
        viewModel.toggleSpanish()

        assertTrue(viewModel.uiState.value.hasUnsavedChanges)
        assertFalse(viewModel.uiState.value.soundEnabled)
        assertEquals(15, viewModel.uiState.value.wordsPerSession)
        assertTrue(viewModel.uiState.value.isSpanishEnabled)

        viewModel.discardChanges()

        assertFalse(viewModel.uiState.value.hasUnsavedChanges)
        assertTrue(viewModel.uiState.value.soundEnabled)
        assertEquals(5, viewModel.uiState.value.wordsPerSession)
        assertFalse(viewModel.uiState.value.isSpanishEnabled)
        // Preferences remain untouched
        assertTrue(appPreferences.soundState)
        assertEquals(5, appPreferences.wordsPerSession)
        assertTrue(appPreferences.secondLanguage.equals("english", ignoreCase = true))
    }

    @Test
    fun `signIn triggers syncManager startSync on success`() {
        val activity = Activity()

        viewModel.signIn(activity)

        assertTrue(viewModel.uiState.value.isSignedIn)
        assertEquals("test-uid-123", fakeSyncManager.lastSyncedUserId)
    }

    // --- Test Doubles ---

    class FakeUserPreferencesRepository(context: android.content.Context) : UserPreferencesRepository(context) {
        var lastDarkMode: Int? = null
        var lastWordsPerSession: Int? = null
        var lastRepetitionPerSession: Int? = null

        override suspend fun setDarkMode(mode: Int) {
            lastDarkMode = mode
        }

        override suspend fun setWordsPerSession(words: Int) {
            lastWordsPerSession = words
        }

        override suspend fun setRepetitionPerSession(repetition: Int) {
            lastRepetitionPerSession = repetition
        }
    }

    class FakeSyncManager : com.fortitude.shamsulkarim.ieltsfordory.data.sync.SyncManager {
        var lastSyncedUserId: String? = null

        override fun startSync(userId: String, callback: FirebaseSyncManager.SyncCallback?) {
            lastSyncedUserId = userId
        }
    }

    class FakeAuthRepository : AuthRepository {
        var user: FirebaseUser? = null

        override fun signIn(activity: Activity, callback: AuthRepository.AuthCallback) {
            val fakeUser = org.mockito.Mockito.mock(FirebaseUser::class.java)
            org.mockito.Mockito.`when`(fakeUser.uid).thenReturn("test-uid-123")
            org.mockito.Mockito.`when`(fakeUser.displayName).thenReturn("Test User")
            org.mockito.Mockito.`when`(fakeUser.email).thenReturn("test@example.com")
            user = fakeUser
            callback.onSuccess(fakeUser)
        }

        override fun signOut() {
            user = null
        }

        override fun getCurrentUser(): FirebaseUser? = user
        override fun isUserAuthenticated(): Boolean = user != null
    }
}
