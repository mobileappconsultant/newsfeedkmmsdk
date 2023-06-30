package com.mobileappconsultant.newsfeedmmsdk.persistence

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

object SummaryCache {
    private val settings: Settings = Settings()

    fun getSummary(key: String): String?  = settings[key]

    fun setSummary(key: String, value: String) {
        settings.putString(key, value)
    }
}