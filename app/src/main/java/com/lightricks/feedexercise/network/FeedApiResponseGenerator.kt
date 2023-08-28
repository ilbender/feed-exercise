package com.lightricks.feedexercise.network

import com.lightricks.feedexercise.network.Constant.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object FeedApiResponseGenerator {

    private val feedApiService : FeedApiService

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    init {
        feedApiService = retrofit.create(FeedApiService::class.java)
    }
    fun createFeedApiService() : FeedApiService {
        return this.feedApiService
    }
}