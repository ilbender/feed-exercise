package com.lightricks.feedexercise.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.lightricks.feedexercise.network.TemplatesMetadataItem
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader


object ConstantItemsApiService : FeedApiService {
    private val fullJson: TemplatesMetadata = JsonToTemplateMetaData.getData()

    override fun getFeedData(): Single<TemplatesMetadata> {
        return Single.create { emitter -> emitter.onSuccess(fullJson) }
    }
}

object IncreasingItemsApiService : FeedApiService {
    private var size: Int = 1
    private val fullLst = JsonToTemplateMetaData.getData().templatesMetadata
    override fun getFeedData(): Single<TemplatesMetadata> {
        val slicedLst: List<TemplatesMetadataItem> = fullLst.take(size)
        size = (size + 1) % (fullLst.size + 1)
        return Single.create { emitter -> emitter.onSuccess(TemplatesMetadata(slicedLst)) }
    }

}

object JsonToTemplateMetaData {

    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<TemplatesMetadata> = moshi.adapter(TemplatesMetadata::class.java)
    fun getData(): TemplatesMetadata {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val inputStream = appContext.assets.open("get_feed_response.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
        return adapter.fromJson(json)!!
    }
}