package com.lightricks.feedexercise.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object MockApiService : FeedApiService{
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter : JsonAdapter<TemplatesMetadata> = moshi.adapter(TemplatesMetadata::class.java)
    override fun getFeedData(): Single<TemplatesMetadata> {
//        val appContext = InstrumentationRegistry.getInstrumentation().context
//        val inputStream = appContext.assets.open("get_feed_response.json")
        val appContext = ApplicationProvider.getApplicationContext<Context>()

        val inputStream = appContext.assets.open("get_feed_response.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
//        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
//        val json = File("/Users/ibender/projects/feed-exercise/app/src/main/assets/get_feed_response.json").readText()
        val response = adapter.fromJson(json)
        return Single.create { emitter -> emitter.onSuccess(response!!) }
    }
}