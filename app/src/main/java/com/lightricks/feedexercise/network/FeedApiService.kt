package com.lightricks.feedexercise.network

import com.lightricks.feedexercise.data.FeedItem
import io.reactivex.Single
import retrofit2.http.GET

/**
 * todo: add the FeedApiService interface and the Retrofit and Moshi code here
 */

interface FeedApiService{

    @GET("Android/demo/feed.json")
    fun getFeedData() : Single<TemplatesMetadata>
}

