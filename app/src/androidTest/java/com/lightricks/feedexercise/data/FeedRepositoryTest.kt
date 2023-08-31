package com.lightricks.feedexercise.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest(private var db : FeedDatabase,
                         private var feedRepository: FeedRepository,
                         private var userProjectList : List<UserProject>) {

    @Before
    fun init(){
        this.db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java).build()
        this.feedRepository = FeedRepository(MockApiService,db)
    }

    @Test
    fun testRefreshSaves(){
        feedRepository.refresh().test().awaitTerminalEvent()
        var fetchedList : List<UserProject>
        db.userProjectDao().getAll().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext{userProjects ->
                fetchedList = userProjects
                Assert.assertEquals(fetchedList,userProjectList)
            }
            .subscribe()
    }
}


