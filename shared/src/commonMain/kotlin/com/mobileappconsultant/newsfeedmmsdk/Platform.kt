package com.mobileappconsultant.newsfeedmmsdk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform