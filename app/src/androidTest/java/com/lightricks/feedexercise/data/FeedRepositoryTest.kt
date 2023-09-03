package com.lightricks.feedexercise.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest {

    private lateinit var db: FeedDatabase
    private lateinit var feedRepository: FeedRepository
    private lateinit var userProjectList: List<UserProject>

    @Before
    fun init(){
         db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java
        ).build()
         feedRepository = FeedRepository(ConstantItemsApiService, db)
        userProjectList = Constans.hardCodedList.map { item ->
            UserProject(item.id, responseUrlToThumbnailUrl(item.thumbnailUrl), item.isPremium)
        }


    }


    @Test
    fun testRefreshSaves() {
        val projectsObserver = feedRepository.getAllProjects().take(1).test()
        projectsObserver.awaitTerminalEvent()
        Assert.assertTrue(projectsObserver.values()[0].isEmpty())
        feedRepository.refresh().test().awaitTerminalEvent()
        val entitiesObserver = db.userProjectDao().getAll().take(1).test()
        entitiesObserver.awaitTerminalEvent()
        Assert.assertEquals(entitiesObserver.values()[0], userProjectList)
    }

    @Test
    fun testGetAll() {
        this.db.userProjectDao().insertAll(userProjectList).test().awaitTerminalEvent()
        val projectsObserver = feedRepository.getAllProjects().take(1).test()
        projectsObserver.awaitTerminalEvent()
        Assert.assertEquals(projectsObserver.values()[0], userProjectList)
    }
}


