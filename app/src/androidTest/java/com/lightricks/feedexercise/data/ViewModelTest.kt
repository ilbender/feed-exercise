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
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewModelTest(
    private var db: FeedDatabase,
    private var feedRepository: FeedRepository,
    private var userProjectList: List<UserProject>
) {

    private lateinit var viewModel: FeedViewModel


    fun init() {
        this.db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java
        ).build()
        this.feedRepository = FeedRepository(ConstantItemsApiService, db)
        val viewModelFactory = FeedViewModelFactory(feedRepository)
        viewModel = viewModelFactory.create(FeedViewModel::class.java)
        Thread.sleep(100)
    }

    fun testViewModelInitData() {
        init()
        val pair: Pair<List<FeedItem>, List<FeedItem>> = setup()
        val viewModelLst: List<FeedItem> = pair.first
        val expectedFullLst = pair.second
        Assert.assertEquals(viewModelLst, expectedFullLst)
    }


    fun testViewModelDeleteData() {
        init()
        deleteFirstProject()
        lateinit var viewModelLst: List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            { feedItems -> viewModelLst = feedItems })
        val expected = userProjectList.map { item ->
            FeedItem(item.id, responseUrlToThumbnailUrl(item.thumbnailUrl), item.isPremium)
        }.drop(1)
        Assert.assertEquals(expected, viewModelLst)
    }


    fun testViewModelUpdateInsertion() {
        init()

        val pair: Pair<List<FeedItem>, List<FeedItem>> = setup()
        val viewModelLst: List<FeedItem> = pair.first
        val expectedFullLst = pair.second
        for (i in 1..expectedFullLst.size) {
            //toHashSet since order doesn't matter and refresh rotates the list due to bonus from
            //last step and its easier this way.
            val expected = expectedFullLst.take(i).toHashSet()
            Assert.assertEquals(viewModelLst.toHashSet(), expected)
            viewModel.refresh()
            Thread.sleep(100) //wait for IO thingies

        }
    }

    private fun deleteFirstProject() {
        val firstProject = this.db.userProjectDao().getById(userProjectList[0].id)
        this.db.userProjectDao().delete(firstProject)
        Thread.sleep(100)
    }


    private fun setup(): Pair< List<FeedItem>, List<FeedItem>> {

        Thread.sleep(100)
        lateinit var viewModelLst: List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            { feedItems -> viewModelLst = feedItems })
        val expectedFullLst = userProjectList.map { item ->
            FeedItem(item.id, responseUrlToThumbnailUrl(item.thumbnailUrl), item.isPremium)
        }
        return Pair( viewModelLst, expectedFullLst)
    }
}