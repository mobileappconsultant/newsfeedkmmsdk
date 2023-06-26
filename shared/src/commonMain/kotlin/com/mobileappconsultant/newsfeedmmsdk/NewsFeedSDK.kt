package com.mobileappconsultant.newsfeedmmsdk

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.mobileappconsultant.newsfeedmmsdk.graphql.AskKoraMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.CompleteRegistrationMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.CreateNewUserMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.ForgotPasswordMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetLatestAndTrendingNewsQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetNewsCategoriesQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetNewsSourcesQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetSavedNewsQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetSingleNewsQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GetUserQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.GoogleLoginMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.LikeNewsMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.LoginMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.LogoutMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.ResendOtpMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.ResetPasswordMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.SaveNewsMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.SeedNewsSourcesQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.VerifyEmailMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.VerifyResetOtpMutation
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.CompleteRegistration
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.CreateUser
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ForgotPassword
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.GoogleAuth
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.Login
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.Logout
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.NewsQuery
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.PromptContent
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.ResetPassword
import com.mobileappconsultant.newsfeedmmsdk.graphql.type.VerifyOtp
import com.mobileappconsultant.newsfeedmmsdk.models.Article
import com.mobileappconsultant.newsfeedmmsdk.models.NewsCategory
import com.mobileappconsultant.newsfeedmmsdk.models.NewsSource
import com.mobileappconsultant.newsfeedmmsdk.models.toProperImageURL
import com.mobileappconsultant.newsfeedmmsdk.persistence.SDKSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

data class TestResponse(
    val fullName: String,
    val email: String
)

object NewsFeedSDK {
    private suspend fun <T : Query.Data, R> makeQuery(query: Query<T>, dataBuilder: (ApolloResponse<T>) -> R?): ApiResponse<R> {
        val token = SDKSettings.getToken()
        val response = Apollo(token).client.query(query).execute()

        return ApiResponse(
            data = dataBuilder(response),
            error = response.errors.isNullOrEmpty().not(),
            errors = response.errors,
        )
    }

    private suspend fun <T : Mutation.Data, R> makeMutation(mutation: Mutation<T>, dataBuilder: (ApolloResponse<T>) -> R?): ApiResponse<R> {
        val token = SDKSettings.getToken()
        val response = Apollo(token).client.mutation(mutation).execute()

        return ApiResponse(
            data = dataBuilder(response),
            error = response.errors.isNullOrEmpty().not(),
            errors = response.errors,
        )
    }

    suspend fun getUser(): ApiResponse<GetUserQuery.Response> {
        return makeQuery(GetUserQuery()) { it.data?.response }
    }

    suspend fun getLatestAndTrendingNews(query: NewsQuery): ApiResponse<List<GetLatestAndTrendingNewsQuery.Response>> {
        return makeQuery(GetLatestAndTrendingNewsQuery(query)) { it.data?.responseFilterNotNull() }
    }

    suspend fun getNewsSources(): ApiResponse<List<GetNewsSourcesQuery.Response>> {
        return makeQuery(GetNewsSourcesQuery()) { it.data?.responseFilterNotNull() }
    }

    suspend fun seedNewsSources(): ApiResponse<List<SeedNewsSourcesQuery.Response>> {
        return makeQuery(SeedNewsSourcesQuery()) { it.data?.responseFilterNotNull() }
    }

    suspend fun getNewsCategories(): ApiResponse<List<GetNewsCategoriesQuery.Response>> {
        return makeQuery(GetNewsCategoriesQuery()) { it.data?.responseFilterNotNull() }
    }

    suspend fun getSingleNews(newsId: String): ApiResponse<GetSingleNewsQuery.Response> {
        return makeQuery(GetSingleNewsQuery(newsId)) { it.data?.response }
    }

    suspend fun getSavedNews(): ApiResponse<List<GetSavedNewsQuery.Response>> {
        return makeQuery(GetSavedNewsQuery()) { it.data?.responseFilterNotNull() }
    }

    suspend fun createNewUser(input: CreateUser): ApiResponse<CreateNewUserMutation.Response> {
        return makeMutation(CreateNewUserMutation(input)) { it.data?.response }
    }

    suspend fun login(input: Login): ApiResponse<LoginMutation.Response> {
        return makeMutation(LoginMutation(input)) { it.data?.response }.apply {
            data?.let {
                SDKSettings.setToken(it.token)
            }
        }
    }

    suspend fun completeRegistration(input: CompleteRegistration): ApiResponse<CompleteRegistrationMutation.Response> {
        return makeMutation(CompleteRegistrationMutation(input)) { it.data?.response }
    }

    suspend fun forgotPassword(input: ForgotPassword): ApiResponse<ForgotPasswordMutation.Response> {
        return makeMutation(ForgotPasswordMutation(input)) { it.data?.response }
    }

    suspend fun googleLogin(input: GoogleAuth): ApiResponse<GoogleLoginMutation.Response> {
        return makeMutation(GoogleLoginMutation(input)) { it.data?.response }.apply {
            data?.let {
                SDKSettings.setToken(it.token)
            }
        }
    }

    suspend fun resetPassword(input: ResetPassword): ApiResponse<ResetPasswordMutation.Response> {
        return makeMutation(ResetPasswordMutation(input)) { it.data?.response }
    }

    suspend fun verifyEmail(input: VerifyOtp): ApiResponse<VerifyEmailMutation.Response> {
        return makeMutation(VerifyEmailMutation(input)) { it.data?.response }
    }

    suspend fun verifyResetOtp(input: VerifyOtp): ApiResponse<VerifyResetOtpMutation.Response> {
        return makeMutation(VerifyResetOtpMutation(input)) { it.data?.response }
    }

    suspend fun logout(input: Logout): ApiResponse<LogoutMutation.Response> {
        return makeMutation(LogoutMutation(input)) { it.data?.response }.apply {
            data?.let {
                SDKSettings.removeToken()
            }
        }
    }

    suspend fun askKora(input: PromptContent): ApiResponse<AskKoraMutation.Response> {
        return makeMutation(AskKoraMutation(input)) { it.data?.response }
    }

    suspend fun saveNews(newsId: String): ApiResponse<Boolean> {
        return makeMutation(SaveNewsMutation(newsId)) { it.data?.response }
    }

    suspend fun likeNews(newsId: String): ApiResponse<Boolean> {
        return makeMutation(LikeNewsMutation(newsId)) { it.data?.response }
    }

    suspend fun resendOtp(email: String): ApiResponse<ResendOtpMutation.Response> {
        return makeMutation(ResendOtpMutation(email)) { it.data?.response }
    }

    suspend fun fetchNewsSources(): ApiResponse<List<NewsSource>> {
        val news = getLatestAndTrendingNews(
            NewsQuery(
                source = optionalOf(null),
                category = optionalOf(null),
                pageSize = optionalOf(10000),
                page = optionalOf(1),
            )
        )

        val newsSources = mutableListOf<NewsSource>()

        val articles = mutableListOf<Article>()

        if (!news.error) {
            articles.addAll(news.data!!.map { Article(
                id = it.id ?: "",
                creators = it.creatorFilterNotNull().orEmpty(),
                title = it.title ?: "",
                description = it.description ?: "",
                imageUrl = it.image_url ?: it.link?.toProperImageURL(),
                link = it.link ?: "",
                sourceId = it.source_id ?: "",
                pubDate = it.pubDate ?: "",
                content = it.content ?: "",
                categories = it.categoryFilterNotNull(),
                likes = it.likesFilterNotNull(),
                isLiked = it.isLiked.orFalse(),
            ) })

            val result = articles.groupBy { it.sourceId }

            result.forEach { item ->
                val response = item.value.groupBy { it.categories.orEmpty()[0] }

                val categories: MutableList<NewsCategory> = mutableListOf()

                response.forEach { cat ->
                    categories.add(NewsCategory(
                        id = cat.key,
                        name = cat.key,
                        articles = cat.value,
                    ))
                }

                val newsSource = NewsSource(
                    id = "",
                    name = item.key,
                    url = item.value.first().link.toProperImageURL(),
                    categories = categories,
                )

                newsSources.add(newsSource)
            }

            return ApiResponse(
                errors = null,
                data = newsSources,
                error = false,
            )
        }

        return ApiResponse(
            errors = news.errors,
            data = null,
            error = true,
        )
    }
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}