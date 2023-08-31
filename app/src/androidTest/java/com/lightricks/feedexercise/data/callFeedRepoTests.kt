package com.lightricks.feedexercise.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.UserProject
import org.junit.Before
import org.junit.Test

class callFeedRepoTests {

    private val hardCodedList : List<UserProject> = listOf(
        UserProject("01E18PGE1RYB3R9YF9HRXQ0ZSD","UnleashThePowerOfNatureThumbnail.jpg",true),
        UserProject("01DX1RB94P35Q1A2W6AA5XCQZ9","AccountingTravisThumbnail.jpg",true),
        UserProject("01EAEFVPZ6MFJEMCA8XB06HB01","yeti-thumbnail.jpg",true),
        UserProject("01DX1RB965Z96AD283559NJT9T","BusinessDevDefaultThumbnail.jpg",true),
        UserProject("01EAEFVXVT6VS2R24GR7Q40XE0","start-living-thumbnail.jpg",true),
        UserProject("01DX1RB94MBAKC6ECYGMKVJT4B","Beach4JulyThumbnail.jpg",false),
        UserProject("01EC34AY3EQTA5294ZRHC6FQMJ","sliderevealboundingbox-happy-independence-day-thumbnail.jpg",true),
        UserProject("01DX1RB923YYSRNFQF90TJNP03","Recipes4JulyThumbnail.jpg",true),
        UserProject("01EBX3ZC3HZEXJYKEQ8SZAMGDY","independence-day-celebration-thumbnail.jpg",true),
        UserProject("01E7674TVS66G1AGGMDE3YJVD7","happy-valentine's-day-thumbnail.jpg",true)
    )

    lateinit var feedRepositoryTest : FeedRepositoryTest

    @Before
    fun setup(){
        val db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            FeedDatabase::class.java).build()
        val feedRepository = FeedRepository(ConstantMockApiService,db)
        this.feedRepositoryTest = FeedRepositoryTest(db,feedRepository,hardCodedList)
    }
    @Test
    fun testFeedRepoSave(){
        this.feedRepositoryTest.testRefreshSaves()
    }
}