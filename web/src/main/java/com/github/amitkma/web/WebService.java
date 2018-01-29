package com.github.amitkma.web;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

/**
 * Created by eddie on 15/1/18.
 */
public class WebService {
    private View mFloatingView;
    WebView wb;

    private final Context mContext;

    public WebService(Context context) {
        mContext = context;
    }

    public View getView() {

        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(mContext).inflate(R.layout.layout_web, null);

        mFloatingView.requestFocus();
        wb = mFloatingView.findViewById(R.id.webView);
        wb.setWebViewClient(new WebViewClient());
        wb.getSettings().setJavaScriptEnabled(true);
        wb.requestFocus();
        wb.setVerticalScrollBarEnabled(false);

        wb.setHorizontalScrollBarEnabled(false);

        wb.loadUrl("https://www.google.co.in/");

        return mFloatingView;
    }
}