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
class BookmarkActivity : AppCompatActivity() {

    /**
     * ViewModelFactory [com.github.amitkma.primeplayer.framework.viewmodel.PrimePlayerViewModelFactory]
     * to provide the ViewModel
     */
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    /**
     * ViewModel to handle view inside this activity.
     */
    private lateinit var bookmarkViewModel: BookmarkViewModel

    @Inject lateinit var bookmarkAdapter: BookmarkAdapter

    private lateinit var bookmarkRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        AndroidInjection.inject(this)
        setSupportActionBar(toolbar)
        initializeView()

        bookmarkViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(BookmarkViewModel::class.java)
        bookmarkViewModel.getBookmarks().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                noBookmarkTextView.visibility = View.GONE
                bookmarkAdapter.list = it
            } else {
                noBookmarkTextView.visibility = View.VISIBLE
            }
        })
    }

    override fun onStart() {
        super.onStart()
        bookmarkViewModel.fetchBookmarks()
    }

    private fun initializeView() {
        bookmarkRecyclerView = findViewById(R.id.bookmarkList)
        bookmarkRecyclerView.layoutManager = StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL)
        bookmarkRecyclerView.adapter = bookmarkAdapter
        bookmarkAdapter.clickListener = { bookmark ->
            run {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("video_path", bookmark.path)
                intent.putExtra("video_name", bookmark.name)
                intent.putExtra("video_thumb", bookmark.thumbnail)
                intent.putExtra("resume_window", bookmark.resumeWindow)
                intent.putExtra("resume_position", bookmark.resumePosition)
                startActivity(intent)
            }
        }
    }
}
