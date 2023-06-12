package com.arkangel.lostintravelsharedlibrary.datasource.network


import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.arkangel.lostintravelsharedlibrary.datasource.persistence.NetworkCache


class Apollo(token: String) {
    private val serverUrl = "https://lostapi.frontendlabs.co.uk/graphql"

    val apolloClient = ApolloClient.Builder().networkTransport(
            HttpNetworkTransport.Builder().addInterceptor(
                interceptor = AuthorizationInterceptor(token)
            ).addInterceptor(LoggingInterceptor())
                .serverUrl(serverUrl)
                .build()
        ).fetchPolicy(FetchPolicy.NetworkFirst)
        .normalizedCache(NetworkCache.cache)
        .build()
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