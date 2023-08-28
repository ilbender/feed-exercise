package com.lightricks.feedexercise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserProject(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "thumbnail_Url") val thumbnailUrl : String,
    @ColumnInfo(name = "is_Premium") val isPremium : Boolean
)
