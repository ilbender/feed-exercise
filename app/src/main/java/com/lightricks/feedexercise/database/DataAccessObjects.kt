package com.lightricks.feedexercise.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UserProjectDao{
    @Query("SELECT * FROM userproject")
    fun getAll() : Observable<List<UserProject>>

    @Query("SELECT * FROM userproject WHERE id IN (:userIds)")
    fun getAllByIds(userIds : List<String>) : Observable<List<UserProject>>

    @Query("SELECT * FROM userproject WHERE id = :uid")
    fun getById(uid : String) : UserProject

    @Query("DELETE FROM userproject")
    fun deleteAll() : Completable
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userProjects: List<UserProject>) : Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProject : UserProject)
    @Delete
    fun delete(userProject: UserProject)

    @Query("SELECT COUNT(*) FROM userproject")
    fun getProjectCount() : Single<Int>
}

