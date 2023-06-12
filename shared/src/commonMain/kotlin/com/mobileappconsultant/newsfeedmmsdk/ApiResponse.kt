package com.mobileappconsultant.newsfeedmmsdk

import com.apollographql.apollo3.api.Error

data class ApiResponse<T>(
    val errors: List<Error>?,
    val data: T? = null,
    val error: Boolean
)
