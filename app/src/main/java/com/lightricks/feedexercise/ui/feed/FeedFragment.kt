package com.lightricks.feedexercise.ui.feed

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.lightricks.feedexercise.R
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.databinding.FeedFragmentBinding
import com.lightricks.feedexercise.network.FeedApiResponseGenerator


/**
 * This Fragment shows the feed grid. The feed consists of template thumbnail images.
 * Layout file: feed_fragment.xml
 */
class FeedFragment : Fragment() {
    // Kotlin note: Usually, variables are initialized at the same time they are declared.
    // Here "lateinit" means that we promise to initialize the variable before accessing it.
    private lateinit var dataBinding: FeedFragmentBinding
    private lateinit var viewModel: FeedViewModel
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false)
        setupViewModel()
        setupViews()
        return dataBinding.root
    }

    private fun setupViewModel() {
        val feedRepository = FeedRepository(FeedApiResponseGenerator.feedApiService, buildDb())
        viewModel = ViewModelProvider(this, FeedViewModelFactory(feedRepository))
            .get(FeedViewModel::class.java)

        viewModel.getFeedItems().observe(viewLifecycleOwner, Observer { items ->
            feedAdapter.items = items
        })

        viewModel.getNetworkErrorEvent().observe(viewLifecycleOwner, Observer { event ->
            // Kotlin note: Here "let { ... }" means that showNetworkError() should be called
            // only if there result of getContentIfNotHandled() is not null.
            event.getContentIfNotHandled()?.let { showNetworkError() }
        })
    }

    private inline fun buildDb(): FeedDatabase {
        val DB_NAME = "user_project_db.db"
        val db = Room.databaseBuilder(
            requireContext().applicationContext as Application,
            FeedDatabase::class.java,
            DB_NAME
        ).build()
        return db
    }

    private fun setupViews() {
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        feedAdapter = FeedAdapter()
        // Kotlin note: object expressions are similar to anonymous classes
        feedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                dataBinding.recyclerView.scrollToPosition(0)
            }
        })
        dataBinding.recyclerView.adapter = feedAdapter
        dataBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun showNetworkError() {
        Snackbar.make(dataBinding.mainContent, R.string.network_error, LENGTH_LONG)
            .show()
    }
}