package com.mobileappconsultant.newsfeedmmsdk


import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.http.LoggingInterceptor


class Apollo(token: String) {
    private val serverUrl = "https://newsfeedapi.frontendlabs.co.uk/query"

    val client = ApolloClient.Builder().networkTransport(
            HttpNetworkTransport.Builder().addInterceptor(
                interceptor = AuthorizationInterceptor(token)
            ).serverUrl(serverUrl).build()
        ).build()
}

class AuthorizationInterceptor(val token: String) : HttpInterceptor {
    private val authorization = "Authorization"

    override suspend fun intercept(
        request: HttpRequest, chain: HttpInterceptorChain
    ): HttpResponse {
        return chain.proceed(request.newBuilder().addHeader(authorization, token)
            .build())
    }
}