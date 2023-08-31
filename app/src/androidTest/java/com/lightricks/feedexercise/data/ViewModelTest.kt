package com.lightricks.feedexercise.data

import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import com.lightricks.feedexercise.ui.feed.FeedViewModel
import com.lightricks.feedexercise.ui.feed.FeedViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        this.feedRepository = FeedRepository(ConstantMockApiService,db)
    }
    @Test
    fun testViewModelInitData(){
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


    fun testViewModelDeleteData(){
        val latch = CountDownLatch(1)

        val viewModelFactory  = FeedViewModelFactory(feedRepository)
        val viewModel : FeedViewModel = viewModelFactory.create(FeedViewModel::class.java)
        viewModel.refresh()
        latch.await(1,TimeUnit.SECONDS)

//        this.db.userProjectDao().insertAll(userProjectList).subscribe()
        latch.await(1,TimeUnit.SECONDS)
        val a = this.db.userProjectDao().getById("01E18PGE1RYB3R9YF9HRXQ0ZSD")
        this.db.userProjectDao().delete(a)
        latch.await(1,TimeUnit.SECONDS)
        lateinit var lst : List<UserProject>

        this.feedRepository.getAllProjects().subscribe { glug -> lst = glug }
        latch.await(1,TimeUnit.SECONDS)
        val b = 0
        latch.await(1, TimeUnit.SECONDS)
        lateinit var viewModelLst : List<FeedItem>
        viewModel.getFeedItems().observe(
            TestLifecycleOwner(),
            {plop -> viewModelLst = plop})
        val c = 0
//        val hardCodedFeedItemsLst = userProjectList.map { item ->
//            FeedItem(item.id,responseUrlToThumbnailUrl(item.thumbnailUrl),item.isPremium) }
//        Assert.assertEquals(feedItemsLst,hardCodedFeedItemsLst.dropLast(1))

    }
}