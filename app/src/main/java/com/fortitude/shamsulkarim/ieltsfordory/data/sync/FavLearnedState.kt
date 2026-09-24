package com.fortitude.shamsulkarim.ieltsfordory.data.sync

/**
 * Model representing user's favorite and learned progress states for cloud sync.
 * Serialized to/from Firebase Realtime Database.
 */
data class FavLearnedState(
    var name: String? = null,
    var ieltsLearnedCount: String? = null,
    var toeflLearnedCount: String? = null,
    var satLearnedCount: String? = null,
    var greLearnedCount: String? = null,
    var ieltsFavCount: String? = null,
    var toeflFavCount: String? = null,
    var satFavCount: String? = null,
    var greFavCount: String? = null
)
