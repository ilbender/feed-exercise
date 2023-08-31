package com.lightricks.feedexercise.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader

object JsonToTemplateMetaData {

    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter : JsonAdapter<TemplatesMetadata> = moshi.adapter(TemplatesMetadata::class.java)
    fun getFeedData(): TemplatesMetadata {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val inputStream = appContext.assets.open("get_feed_response.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
        return adapter.fromJson(json)!!
    }
}