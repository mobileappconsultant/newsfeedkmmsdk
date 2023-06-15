package com.mobileappconsultant.newsfeedmmsdk.persistence

import com.russhwolf.settings.Settings

object SDKSettings {
    private val settings: Settings = Settings()

    fun setToken(token: String) {
        settings.putString("auth_token", token)
    }

    fun getToken() = settings.getString("auth_token", "")

    fun removeToken() {
        settings.remove("auth_token")
    }
}