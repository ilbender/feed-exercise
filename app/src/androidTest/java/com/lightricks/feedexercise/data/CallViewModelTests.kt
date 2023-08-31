package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lightricks.feedexercise.data.Constans.hardCodedList
import com.lightricks.feedexercise.database.FeedDatabase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallViewModelTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    lateinit var db: FeedDatabase
    lateinit var viewModelTest: ViewModelTest

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java
        ).build()

    }

    @Test
    fun testViewModelInit() {
        val feedRepository = FeedRepository(ConstantItemsApiService, db)
        this.viewModelTest = ViewModelTest(db, feedRepository, hardCodedList)
        this.viewModelTest.testViewModelInitData()
    }

    @Test
    fun testViewModelDelete() {
        val feedRepository = FeedRepository(ConstantItemsApiService, db)
        this.viewModelTest = ViewModelTest(db, feedRepository, hardCodedList)
        this.viewModelTest.testViewModelDeleteData()
    }

    @Test
    fun testViewModelUpdateInsertion(){
        val feedRepository = FeedRepository(IncreasingItemsApiService, db)
        this.viewModelTest = ViewModelTest(db, feedRepository, hardCodedList)
        this.viewModelTest.testViewModelUpdateInsertion()
    }
}