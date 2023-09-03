package com.lightricks.feedexercise.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest {
    private val db: FeedDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        FeedDatabase::class.java
    ).build()
    private val feedRepository: FeedRepository = FeedRepository(ConstantItemsApiService, db)
    private val userProjectList: List<UserProject> = Constans.hardCodedList.map { item ->
        UserProject(item.id, responseUrlToThumbnailUrl(item.thumbnailUrl), item.isPremium)
    }


    @Test
    fun testRefreshSaves() {
        var fetchedList: List<UserProject>
        feedRepository.getAllProjects().doOnNext { userProjects ->
            fetchedList = userProjects
            Assert.assertTrue(fetchedList.isEmpty())
        }.subscribe()
        feedRepository.refresh().test().awaitTerminalEvent()
        db.userProjectDao().getAll().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { userProjects ->
                fetchedList = userProjects
                Assert.assertEquals(fetchedList, userProjectList)
            }
            .subscribe()
    }

    @Test
    fun testGetAll() {
        this.db.userProjectDao().insertAll(userProjectList).test().awaitTerminalEvent()
        var fetchedList: List<UserProject>
        feedRepository.getAllProjects().subscribe { fetchedUserProjects ->
            fetchedList = fetchedUserProjects
            Assert.assertEquals(fetchedList, userProjectList)
        }


    }
}


