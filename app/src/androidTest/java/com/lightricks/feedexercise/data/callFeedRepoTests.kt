package com.lightricks.feedexercise.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lightricks.feedexercise.data.Constans.hardCodedList
import com.lightricks.feedexercise.database.FeedDatabase
import org.junit.Before
import org.junit.Test

class callFeedRepoTests {

    lateinit var feedRepositoryTest: FeedRepositoryTest

    @Before
    fun setup() {
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java
        ).build()
        val feedRepository = FeedRepository(ConstantItemsApiService, db)
        this.feedRepositoryTest = FeedRepositoryTest(db, feedRepository, hardCodedList)
    }

    @Test
    fun testFeedRepoSave() {
        this.feedRepositoryTest.testRefreshSaves()
    }

    @Test
    fun testFeedRepoGet(){
        this.feedRepositoryTest.testGetAll()
    }
}