package com.lightricks.feedexercise.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lightricks.feedexercise.R
import com.lightricks.feedexercise.ui.feed.FeedFragment


/**
 * This is the main entry point into the app. This activity shows the [FeedFragment].
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }



}