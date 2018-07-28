package com.github.amitkma.primeplayer.features.bookmarks

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.videoplayer.VideoPlayerActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 */
class HighlighterActivity : AppCompatActivity() {

    /**
     * ViewModelFactory [com.github.amitkma.primeplayer.framework.viewmodel.PrimePlayerViewModelFactory]
     * to provide the ViewModel
     */
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    /**
     * ViewModel to handle view inside this activity.
     */
    private lateinit var highlighterViewModel: HighlighterViewModel

    @Inject lateinit var highlighterAdapter: HighlighterAdapter

    private lateinit var highlighterRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlighter)

        AndroidInjection.inject(this)
        setSupportActionBar(toolbar)
        initializeView()

        highlighterViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HighlighterViewModel::class.java)
        highlighterViewModel.getHighlightedItems().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                noBookmarkTextView.visibility = View.GONE
                highlighterAdapter.list = it
            } else {
                noBookmarkTextView.visibility = View.VISIBLE
            }
        })
    }

    override fun onStart() {
        super.onStart()
        highlighterViewModel.fetchBookmarks()
    }

    private fun initializeView() {
        highlighterRecyclerView = findViewById(R.id.highlightedList)
        highlighterRecyclerView.layoutManager = StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL)
        highlighterRecyclerView.adapter = highlighterAdapter
        highlighterAdapter.clickListener = { highlightedItems ->
            run {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("video_path", highlightedItems.path)
                intent.putExtra("video_name", highlightedItems.name)
                intent.putExtra("video_thumb", highlightedItems.thumbnail)
                intent.putExtra("resume_window", highlightedItems.startWindow)
                intent.putExtra("resume_position", highlightedItems.startPosition)
                intent.putExtra("stop_window", highlightedItems.stopWindow)
                intent.putExtra("stop_position", highlightedItems.stopPosition)
                startActivity(intent)
            }
        }
    }
}
