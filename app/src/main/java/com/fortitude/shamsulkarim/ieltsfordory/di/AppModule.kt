package com.fortitude.shamsulkarim.ieltsfordory.di

import com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase.FirebaseAudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.media.firebase.FirebaseImageRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.database.firebase.FirebaseDatabaseRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.learning.sql.SqlLearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.data.vocabulary.AggregatedVocabularyRepository
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
import org.koin.dsl.module

val appModule = module {
    single<AudioRepository> { FirebaseAudioRepository() }
    single<ImageRepository> { FirebaseImageRepository() }
    single<DatabaseRepository> { FirebaseDatabaseRepository(get()) }
    single<LearningRepository> { SqlLearningRepository(get()) }
    single<VocabularyRepository> { AggregatedVocabularyRepository(get()) }
    factory { DownloadAudioUseCase(get()) }
    factory { DownloadImageUseCase(get()) }
    factory { UpdateUserDataUseCase(get()) }
    factory { AddChildEventListenerUseCase(get()) }
    factory { RemoveChildEventListenerUseCase(get()) }
    factory { GetFavLearnedStateUseCase(get()) }
    factory { FetchSessionWordsUseCase(get()) }
    factory { GetAllUnlearnedWordsUseCase(get()) }
    factory { UpdateLearnedStatusUseCase(get()) }
    factory { UpdateJustLearnedStatusUseCase(get()) }
    factory { UpdateFavoriteStatusUseCase(get()) }
    factory { UpdateLearnedStatusSingleUseCase(get()) }
    factory { GetJustLearnedSessionDataUseCase(get()) }
    factory { GetVocabularyUseCase(get()) }
    factory { GetFavoriteWordsUseCase(get()) }
    factory { GetLearnedWordsUseCase(get()) }
    factory { GetUnlearnedWordsUseCase(get()) }
    factory { GetLearnedCountUseCase(get()) }
    factory { GetTotalCountUseCase(get()) }
    factory { UpdateFavoriteStateUseCase(get()) }
    factory { UpdateLearnStateUseCase(get()) }
}
