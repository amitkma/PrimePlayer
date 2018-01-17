package com.github.amitkma.primeplayer.features.videos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.amitkma.primeplayer.R
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
import kotlinx.android.synthetic.main.activity_video_player.*
import timber.log.Timber
import java.io.File

/**
 * Created by falcon on 17/1/18.
 */
class VideoPlayerActivity : AppCompatActivity() {

    private var exoPlayer: SimpleExoPlayer? = null

    private var shouldAutoPlay: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        releasePlayer()
        shouldAutoPlay = true
        setContentView(R.layout.activity_video_player)
        initializePlayer()
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector())
        exoPlayer!!.addListener(eventListener)
        val uri = Uri.fromFile(File(intent.getStringExtra("video_uri")))
        val dataSpec = DataSpec(uri)
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSource.FileDataSourceException) {
            e.printStackTrace()
        }

        val factory: DataSource.Factory = DataSource.Factory { fileDataSource }
        val videoSource: MediaSource = ExtractorMediaSource.Factory(factory).createMediaSource(uri)
        playerView.player = exoPlayer
        exoPlayer!!.playWhenReady = shouldAutoPlay
        exoPlayer!!.prepare(videoSource, true, false)

    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            shouldAutoPlay = exoPlayer!!.playWhenReady
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
        }

    }
}