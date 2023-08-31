package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.data.Constans.hardCodedList
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.ui.feed.FeedViewModel
import com.lightricks.feedexercise.ui.feed.FeedViewModelFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewModelTest {
    private lateinit var db: FeedDatabase
    private lateinit var constantfeedRepository: FeedRepository
    private lateinit var increasingFeedRepo: FeedRepository
    private val expectedFullLst = hardCodedList.map { item ->
        FeedItem(item.id, responseUrlToThumbnailUrl(item.thumbnailUrl), item.isPremium)
    }

    private lateinit var viewModel: FeedViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun init() {
        this.db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java
        ).build()
        this.constantfeedRepository = FeedRepository(ConstantItemsApiService, db)
        this.increasingFeedRepo = FeedRepository(IncreasingItemsApiService, db)
    }

    @Test
    fun testViewModelInitData() {
        init()
        val viewModelFactory = FeedViewModelFactory(constantfeedRepository)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        Thread.sleep(100)
        lateinit var viewModelLst: List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            { feedItems -> viewModelLst = feedItems })
        Assert.assertEquals(viewModelLst, expectedFullLst)
    }


    @Test
    fun testViewModelDeleteData() {
        init()
        val viewModelFactory = FeedViewModelFactory(constantfeedRepository)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        Thread.sleep(100)
        deleteFirstProject()
        lateinit var viewModelLst: List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            { feedItems -> viewModelLst = feedItems })
        val expected = expectedFullLst.drop(1)
        Assert.assertEquals(expected, viewModelLst)
    }

    @Test
    fun testViewModelUpdateInsertion() {
        init()
        val viewModelFactory = FeedViewModelFactory(increasingFeedRepo)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        Thread.sleep(100)
        lateinit var viewModelLst: List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            { feedItems -> viewModelLst = feedItems })

        for (i in 1..expectedFullLst.size) {
            //toHashSet since order doesn't matter and refresh rotates the list due to bonus from
            //last step and its easier this way.
            val expected = expectedFullLst.take(i).toHashSet()
            Assert.assertEquals(viewModelLst.toHashSet(), expected)
            viewModel.refresh()
            Thread.sleep(200) //wait for IO thingies

        }
    }

    private fun deleteFirstProject() {
        Thread.sleep(100)
        val firstProject = this.db.userProjectDao().getById(expectedFullLst[0].id)
        this.db.userProjectDao().delete(firstProject)
        Thread.sleep(100)
    }
}