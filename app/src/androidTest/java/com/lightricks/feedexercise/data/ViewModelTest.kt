package com.lightricks.feedexercise.data

import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import com.lightricks.feedexercise.ui.feed.FeedViewModel
import com.lightricks.feedexercise.ui.feed.FeedViewModelFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ViewModelTest (private var db : FeedDatabase,
                     private var feedRepository: FeedRepository,
                     private var userProjectList : List<UserProject>){



    @Before
    fun init(){
        this.db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java).build()
        this.feedRepository = FeedRepository(MockApiService,db)
    }
    @Test
    fun testViewModelData(){
        val latch = CountDownLatch(1)
        val viewModelFactory  = FeedViewModelFactory(feedRepository)
        val viewModel : FeedViewModel = viewModelFactory.create(FeedViewModel::class.java)
        latch.await(1, TimeUnit.SECONDS)
        lateinit var feedItemsLst : List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            {glug -> feedItemsLst = glug})
        val hardCodedFeedItemsLst = userProjectList.map { item ->
            FeedItem(item.id,responseUrlToThumbnailUrl(item.thumbnailUrl),item.isPremium) }
        Assert.assertEquals(feedItemsLst,hardCodedFeedItemsLst)
    }
}