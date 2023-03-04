package com.mobileappconsultant.newsfeedmmsdk

import com.apollographql.apollo3.ApolloClient

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
//        val client = ApolloClient.Builder()
//            .serverUrl("")
//            .build()

        return "Hello, ${platform.name}!"
    }
}