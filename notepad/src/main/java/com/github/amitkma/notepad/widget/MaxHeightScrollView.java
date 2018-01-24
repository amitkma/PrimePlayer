package com.github.amitkma.notepad.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.github.amitkma.notepad.Notepad;

public class MaxHeightScrollView extends ScrollView {
	public MaxHeightScrollView(Context context) {
		super(context);
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("NewApi")
	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(Notepad.DEVICE_HEIGHT / 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
