package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import com.lightricks.feedexercise.network.Constant
import com.lightricks.feedexercise.network.FeedApiService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(
    private val feedApiService: FeedApiService,
    private val feedDatabase: FeedDatabase
) {

    private var rotationNumber: Int = 0


    fun refresh(): Completable {
        return feedApiService.getFeedData().subscribeOn(Schedulers.io()).flatMapCompletable { feedData ->
            feedDatabase.userProjectDao().insertAll(rotateList(feedData.templatesMetadata.map { item ->
                UserProject(item.id, responseUrlToThumbnailUrl(item.templateThumbnailURI), item.isPremium)
            }))
        }.observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllProjects(): Observable<List<UserProject>> {

        return feedDatabase.userProjectDao().getAll()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun rotateList(originalList : List<UserProject>) : List<UserProject>{
        val currentRotations = this.rotationNumber % originalList.size
        this.rotationNumber = this.rotationNumber + 1
        return originalList.takeLast(currentRotations) + originalList.dropLast(currentRotations)
    }
}

fun responseUrlToThumbnailUrl(templateThumbnailURI: String): String {
    return Constant.BASE_THUMBNAIL_URL + templateThumbnailURI
}
