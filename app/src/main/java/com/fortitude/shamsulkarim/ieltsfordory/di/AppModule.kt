package com.fortitude.shamsulkarim.ieltsfordory.di

import androidx.room.Room
import com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase.FirebaseAudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase.FirebaseImageRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.database.firebase.FirebaseDatabaseRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.VocabularyDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data.database.migration.LegacyMigrationHelper
import com.fortitude.shamsulkarim.ieltsfordory.data.learning.room.RoomLearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.vocabulary.room.RoomVocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.connectivity.AndroidConnectivityRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.tts.AndroidTtsRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.auth.FirebaseAuthRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.ImageRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadAudioUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadImageUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.DatabaseRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.UpdateUserDataUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.RemoveChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetFavLearnedStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.FetchSessionWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetAllUnlearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateJustLearnedStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusSingleUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.GetJustLearnedSessionDataUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetVocabularyUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetFavoriteWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetUnlearnedWordsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetLearnedCountUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetTotalCountUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.TtsRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.IsTtsReadyUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.SpeakTextUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.StopTtsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.ShutdownTtsUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.ConnectivityRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.AuthRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.SignInUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.SignOutUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.GetCurrentUserUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.auth.usecase.IsUserAuthenticatedUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ========== Room Database ==========
    single {
        Room.databaseBuilder(
            get(),
            VocabularyDatabase::class.java,
            "vocabulary_db"
        ).build()
    }

    // Room DAOs
    single { get<VocabularyDatabase>().wordProgressDao() }
    single { get<VocabularyDatabase>().sessionWordDao() }

    // Legacy Migration Helper
    single { LegacyMigrationHelper(get(), get()) }

    // ========== Repositories ==========
    single<AudioRepository> { FirebaseAudioRepository() }
    single<ImageRepository> { FirebaseImageRepository() }
    single<DatabaseRepository> { FirebaseDatabaseRepository(get()) }
    
    // Room-backed repositories (replacing legacy SQLite)
    single<VocabularyRepository> { RoomVocabularyRepository(get(), get()) }
    single<LearningRepository> { RoomLearningRepository(get(), get(), get(), get()) }
    
    single<TtsRepository> { AndroidTtsRepository(get()) }
    single<ConnectivityRepository> { AndroidConnectivityRepository(get()) }
    single<AuthRepository> { FirebaseAuthRepository(get()) }

    // ========== Use Cases ==========
    // Media
    factory { DownloadAudioUseCase(get()) }
    factory { DownloadImageUseCase(get()) }
    
    // Database
    factory { UpdateUserDataUseCase(get()) }
    factory { AddChildEventListenerUseCase(get()) }
    factory { RemoveChildEventListenerUseCase(get()) }
    
    // Learning
    factory { GetFavLearnedStateUseCase(get()) }
    factory { FetchSessionWordsUseCase(get()) }
    factory { GetAllUnlearnedWordsUseCase(get()) }
    factory { UpdateLearnedStatusUseCase(get()) }
    factory { UpdateJustLearnedStatusUseCase(get()) }
    factory { UpdateFavoriteStatusUseCase(get()) }
    factory { UpdateLearnedStatusSingleUseCase(get()) }
    factory { GetJustLearnedSessionDataUseCase(get()) }
    
    // Vocabulary
    factory { GetVocabularyUseCase(get()) }
    factory { GetFavoriteWordsUseCase(get()) }
    factory { GetLearnedWordsUseCase(get()) }
    factory { GetUnlearnedWordsUseCase(get()) }
    factory { GetLearnedCountUseCase(get()) }
    factory { GetTotalCountUseCase(get()) }
    factory { UpdateFavoriteStateUseCase(get()) }
    factory { UpdateLearnStateUseCase(get()) }
    
    // TTS
    factory { IsTtsReadyUseCase(get()) }
    factory { SpeakTextUseCase(get()) }
    factory { StopTtsUseCase(get()) }
    factory { ShutdownTtsUseCase(get()) }
    
    // Connectivity
    factory { IsConnectedUseCase(get()) }
    
    // Auth
    factory { SignInUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { IsUserAuthenticatedUseCase(get()) }

    // ========== ViewModels ==========
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.main.MainViewModel(
            get(), get(), get(), get(), get()
        )
    }
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.home.HomeViewModel(
            get(), get(), 
            com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences.get(get())
        )
    }
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.words.AllWordsViewModel(
            get(), get(), get(), get(), get(), get(),
            com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences.get(get())
        )
    }
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.learned.LearnedViewModel(
            get(), get(), get(), get(), get(), get(),
            com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences.get(get())
        )
    }
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.favorites.FavoriteViewModel(
            get(), get(), get(), get(), get(), get(),
            com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences.get(get())
        )
    }
    viewModel { 
        com.fortitude.shamsulkarim.ieltsfordory.ui.profile.ProfileViewModel(
            com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences.get(get()),
            get()
        )
    }
}

