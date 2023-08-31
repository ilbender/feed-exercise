package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.lightricks.feedexercise.network.TemplatesMetadataItem
import io.reactivex.Single


object ConstantMockApiService : FeedApiService{
    private val fullJson = JsonToTemplateMetaData.getFeedData()

    override fun getFeedData(): Single<TemplatesMetadata> {
        return Single.create { emitter -> emitter.onSuccess(fullJson) }
    }
}

object IncreasingItemsApiService : FeedApiService{
    private var size : Int = 1
    private val fullLst = JsonToTemplateMetaData.getFeedData().templatesMetadata
    override fun getFeedData(): Single<TemplatesMetadata> {
        val slicedLst : List<TemplatesMetadataItem> = fullLst.take(size)
        size = (size + 1) % fullLst.size
        return Single.create{emitter -> emitter.onSuccess(TemplatesMetadata(slicedLst))}
    }

}