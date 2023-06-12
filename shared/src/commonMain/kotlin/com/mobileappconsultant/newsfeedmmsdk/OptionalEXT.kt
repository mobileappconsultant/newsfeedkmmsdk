package com.mobileappconsultant.newsfeedmmsdk

import com.apollographql.apollo3.api.Optional

fun<T> optionalOf(value: T?): Optional<T> {
    return Optional.presentIfNotNull(value)
}
