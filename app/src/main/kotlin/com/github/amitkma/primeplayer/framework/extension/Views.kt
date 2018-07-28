package com.github.amitkma.primeplayer.framework.extension

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Created by falcon on 15/1/18.
 */

// Function to cancel view transition.
fun View.cancelTransition() {
    transitionName = null
}

// Function to toggle visibility.
fun View.visible() = this.visibility == View.VISIBLE

// Function to inflate a layout.
fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View = LayoutInflater.from(context)
        .inflate(layoutRes, this, false)

// Function to load image from a url using Glide.
fun ImageView.loadFromUrl(url: String) = Glide.with(this.context.applicationContext)
        .load(url).transition(DrawableTransitionOptions.withCrossFade()).into(this)!!
// Function to load image from a uri using Glide
fun ImageView.loadFromUri(path: String) = Glide.with(this.context.applicationContext)
        .load("file"+path).transition(DrawableTransitionOptions.withCrossFade()).into(this)!!