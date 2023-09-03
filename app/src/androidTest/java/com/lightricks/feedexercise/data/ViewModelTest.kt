package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        val viewModelFactory = FeedViewModelFactory(constantfeedRepository)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        while (viewModel.getFeedItems().blockingObserve()!!.size == 0){
            Thread.sleep(10)
        }
        val actual = viewModel.getFeedItems().blockingObserve()!!
        Assert.assertEquals(actual, expectedFullLst)
    }


    @Test
    fun testViewModelDeleteData() {
        val viewModelFactory = FeedViewModelFactory(constantfeedRepository)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        while (viewModel.getFeedItems().blockingObserve()!!.size == 0){
            Thread.sleep(10)
        }
        var viewModelLst: List<FeedItem> = viewModel.getFeedItems().blockingObserve()!!
        Assert.assertEquals(expectedFullLst,viewModelLst)
        deleteFirstProject()
        while (viewModelLst.size == expectedFullLst.size){
            viewModelLst = viewModel.getFeedItems().blockingObserve()!!
            Thread.sleep(10)
        }
        val expected = expectedFullLst.drop(1)
        Assert.assertEquals(expected, viewModelLst)
    }

    @Test
    fun testViewModelRefreshNewData() {
        val viewModelFactory = FeedViewModelFactory(increasingFeedRepo)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)

        for (i in 1..expectedFullLst.size) {
            var viewModelLst: List<FeedItem> = viewModel.getFeedItems().blockingObserve()!!
            while(viewModelLst.size < i){
                viewModelLst = viewModel.getFeedItems().blockingObserve()!!
                Thread.sleep(10)
            }
            //toHashSet since order doesn't matter and refresh rotates the list due to bonus from
            //last step and its easier this way.
            val expected = expectedFullLst.take(i).toHashSet()
            Assert.assertEquals(viewModelLst.toHashSet(), expected)
            viewModel.refresh()
        }
    }

    private fun deleteFirstProject() {
        val firstProject = this.db.userProjectDao().getById(expectedFullLst[0].id)
        this.db.userProjectDao().delete(firstProject)
    }

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                value = t
                latch.countDown()
                removeObserver(this)
            }
        }

        observeForever(observer)
        latch.await(5, TimeUnit.SECONDS)
        return value
    }

}