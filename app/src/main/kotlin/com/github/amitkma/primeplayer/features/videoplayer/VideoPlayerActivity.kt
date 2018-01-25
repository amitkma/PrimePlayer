package com.github.amitkma.primeplayer.features.videoplayer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import com.github.amitkma.calculator.Calculator
import com.github.amitkma.dictionary.Dictionary
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.addbookmarks.AddBookmarkDialog
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.framework.extension.convertToPx
import com.github.amitkma.primeplayer.framework.extension.convertToString
import com.github.amitkma.web.WebService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.Util
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.item_playback_control.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 */
class VideoPlayerActivity : AppCompatActivity(), AddBookmarkDialog.AddBookmarkDialogListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var videoPlayerViewModel: VideoPlayerViewModel

    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var videoName: String

    private lateinit var thumbnail: String

    private lateinit var path: String

    private var resumePosition: Long = 0

    private var resumeWindow: Int = 0

    private var handler: Handler? = null

    private var shouldPlay: Boolean = true

    private var mBound: Boolean = false
    @Inject
    lateinit var popupBookmarkAdapter: PopupBookmarkAdapter

    private var popupWindow: PopupWindow? = null
    private val TAG: String = "VideoPlayerActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_video_player)
        AndroidInjection.inject(this)
        clearPlayerParams()
        if (intent != null) {
            Timber.d("intent not null")
            path = intent.getStringExtra("video_path")
            videoName = intent.getStringExtra("video_name")
            thumbnail = intent.getStringExtra("video_thumb")
            resumeWindow = intent.getIntExtra("resume_window", C.INDEX_UNSET)
            resumePosition = intent.getLongExtra("resume_position", C.TIME_UNSET)
        }
        videoPlayerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VideoPlayerViewModel::class.java)
        initializeView()
    }

    private fun clearPlayerParams() {
        resumeWindow =  C.INDEX_UNSET
        resumePosition = C.TIME_UNSET
        shouldPlay = true
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ");
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ");
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ");
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ");
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ");
    }

    private fun initializeView() {

        addBookmarkImageButton.setOnClickListener {
            resumeWindow = exoPlayer!!.currentWindowIndex
            resumePosition = Math.max(0, exoPlayer!!.contentPosition)
            shouldPlay = exoPlayer!!.playWhenReady
            setPlayPause(false)
            val dialogFragment = AddBookmarkDialog.newInstance(
                    videoName + " Bookmark")
            dialogFragment.show(fragmentManager, "bookmark_dialog")
        }

        playerView.setOnTouchListener({ _, e ->
            if (mBound) {
                unbindService(connection)
                mBound = false
            } else if (popupWindow != null) {
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                playerView.onTouchEvent(e)
            }
            true
        })

        calculatorImageView.setOnClickListener {
            if (!mBound) {
                val intent = Intent(this, Calculator::class.java)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            } else if (popupWindow != null) {
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                unbindService(connection)
                mBound = false
            }
        }

        dictionaryImageView.setOnClickListener {
            if (!mBound) {
                val intent = Intent(this, Dictionary::class.java)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            } else if (popupWindow != null) {
                popupWindow!!.dismiss()
                popupWindow = null

            } else {
                unbindService(connection)
                mBound = false
            }
        }

        searchImageView.setOnClickListener {
            if (!mBound) {
                val intent = Intent(this, WebService::class.java)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            } else if (popupWindow != null) {
                popupWindow!!.dismiss()
                popupWindow = null

            } else {
                unbindService(connection)
                mBound = false
            }
        }

        bookmarkListImageView.setOnClickListener {
            if (mBound) {
                unbindService(connection)
                mBound = false
            } else if (popupWindow != null) {
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                createBookmarkPopupWindow()
            }
        }
    }

    private fun createBookmarkPopupWindow() {
        try {
            val inflater: LayoutInflater = this.getSystemService(
                    android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.popup_bookmark, null)
            popupWindow = PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, false)
            popupWindow!!.isOutsideTouchable = true
            popupWindow!!.width = 360.convertToPx().toInt()
            val recyclerView = view.findViewById<RecyclerView>(R.id.bookmarkPopupRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = popupBookmarkAdapter
            popupBookmarkAdapter.clickListener = {
                exoPlayer!!.seekTo(it.resumeWindow, it.resumePosition)
            }
            val observer = Observer<List<Bookmark>> {
                if (it != null && it.isNotEmpty()) {
                    popupBookmarkAdapter.list = it
                } else if (it != null && it.isEmpty()) {
                    popupWindow!!.dismiss()
                    Toast.makeText(this, "No bookmark for this video", Toast.LENGTH_SHORT).show()
                }
            }
            videoPlayerViewModel.getVideoBookmarks(path).observe(this, observer)

            popupWindow!!.setOnDismissListener {
                if (videoPlayerViewModel.getVideoBookmarks(path).hasObservers()) {
                    videoPlayerViewModel.getVideoBookmarks(path).removeObserver(observer)
                }
            }
            popupWindow!!.showAtLocation(bookmarkListImageView, Gravity.RIGHT, 0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        }

    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector())
        exoPlayer!!.addListener(eventListener)

        val uri = Uri.fromFile(File(path))
        val dataSource: MediaSource = prepareDataSource(uri)

        playerView.player = exoPlayer
        setPlayPause(shouldPlay)

        val haveResumePosition: Boolean = resumeWindow != C.INDEX_UNSET
        if (haveResumePosition) {
            exoPlayer!!.seekTo(resumeWindow, resumePosition)
        }

        mediaControllerProgress.requestFocus()
        mediaControllerProgress.progress = 0
        mediaControllerProgress.max = (exoPlayer!!.duration / 1000).toInt()
        mediaControllerProgress.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                            fromUser: Boolean) {
                        if (!fromUser) {
                            return
                        }
                        exoPlayer!!.seekTo((progress * 1000).toLong())
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                })
        exoPlayer!!.prepare(dataSource, !haveResumePosition, false)
    }

    private fun prepareDataSource(uri: Uri): MediaSource {
        val dataSpec = DataSpec(uri)
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSource.FileDataSourceException) {
            e.printStackTrace()
        }

        val factory: DataSource.Factory = DataSource.Factory { fileDataSource }
        return ExtractorMediaSource.Factory(factory).createMediaSource(uri)
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            shouldPlay = exoPlayer!!.playWhenReady
            resumeWindow = exoPlayer!!.currentWindowIndex
            resumePosition = Math.max(0, exoPlayer!!.contentPosition)
            exoPlayer!!.release()
            exoPlayer = null
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            Timber.d("playbackParameters " + String.format(
                    "[speed=%.2f, pitch=%.2f]", playbackParameters!!.speed,
                    playbackParameters.pitch))
        }

        override fun onSeekProcessed() {
            Timber.d("seekProcessed")
            setPlayerProgress()
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?) {
            Timber.d("onTracksChanged")
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Timber.e(error, "playerError")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Timber.d("Loading [$isLoading]")
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Timber.d("positionDiscontinuity [$reason]")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Timber.d("repeatMode [$repeatMode]")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Timber.d("shuffleModeEnabled [$shuffleModeEnabled]")
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Timber.d("onTimelineChanged")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Timber.d("state [playWhenReady = $playWhenReady, playbackState = $playbackState]")
            when (playbackState) {
                Player.STATE_ENDED -> {
                    setPlayPause(false)
                    exoPlayer!!.seekTo(0)
                }
                Player.STATE_READY -> {
                    setPlayerProgress()
                }
                Player.STATE_BUFFERING -> {
                    Timber.d("Playback buffering")
                }
                Player.STATE_IDLE -> {
                    Timber.d("Playback idle")
                }
            }
        }
    }

    private fun setPlayerProgress() {
        currentTimeTextView.text = exoPlayer!!.contentPosition.toInt().convertToString()

        if (handler == null) handler = Handler()

        handler!!.post(object : Runnable {
            override fun run() {
                if (exoPlayer != null) {
                    mediaControllerProgress.max = (exoPlayer!!.duration / 1000).toInt()
                    mediaControllerProgress.progress = (exoPlayer!!.contentPosition / 1000).toInt()
                    currentTimeTextView.text = exoPlayer!!.contentPosition.toInt().convertToString()
                    endTimeTextView.text = exoPlayer!!.duration.toInt().convertToString()
                    if(exoPlayer!!.playWhenReady){
                        handler!!.postDelayed(this, 1000)
                    }
                }
            }

        })
    }

    private fun setPlayPause(play: Boolean) {
        exoPlayer!!.playWhenReady = play
    }

    override fun onDialogAddClick(bookmarkName: String) {
        setPlayPause(shouldPlay)
        videoPlayerViewModel.addBookmark(path, bookmarkName, thumbnail, resumeWindow,
                resumePosition)
        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
    }

    override fun onDialogCancelClick() {
        setPlayPause(shouldPlay)
        Toast.makeText(this, "Bookmark cancelled", Toast.LENGTH_SHORT).show()
    }
}