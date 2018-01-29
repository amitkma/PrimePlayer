package com.github.amitkma.dictionary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amitkma.dictionary.api.OxfordApiService;
import com.github.amitkma.dictionary.model.LexicalEntry;
import com.github.amitkma.dictionary.model.MeaningModel;
import com.github.amitkma.dictionary.model.OxfordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kuldeep on 19/1/18.
 */

public class Dictionary {
    private WindowManager mWindowManager;
    public View mFloatingView;
    EditText mEditText;
    ImageView mImageButton;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private List<MeaningModel> mMeaningList;
    private MeaningAdapter mMeaningAdapter;
    private FrameLayout mNoConnection;
    private final Context mContext;

    private static final String TAG = "Dictionary";

    public Dictionary(Context context) {
        this.mContext = context;
    }

    public View getView() {

        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(mContext).inflate(R.layout.layout_dictionary, null);

        mEditText = mFloatingView.findViewById(R.id.editText);
        mImageButton = mFloatingView.findViewById(R.id.button);
        mFloatingView.findViewById(R.id.refreshButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeRequest();
                    }
                });
        mRecyclerView = mFloatingView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mMeaningList = new ArrayList<>();
        mMeaningAdapter = new MeaningAdapter(mMeaningList);
        mRecyclerView.setAdapter(mMeaningAdapter);
        mProgressBar = mFloatingView.findViewById(R.id.loadingBar);
        mNoConnection = mFloatingView.findViewById(R.id.noConnectionView);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
        return  mFloatingView;
    }

    private void makeRequest() {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnection.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mEditText.getText().toString().matches("")) {
                Toast.makeText(mFloatingView.getContext(), "No word to search",
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            } else {
                makeApiRequest();
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mNoConnection.setVisibility(View.VISIBLE);
        }
    }

    private void makeApiRequest() {
        OxfordApiService.Creator.makeOxfordService().getWordDetails(
                mEditText.getText().toString().trim())
                .enqueue(new Callback<OxfordModel>() {
                    @Override
                    public void onResponse(Call<OxfordModel> call, Response<OxfordModel> response) {
                        Log.d(TAG, "onResponse() called with: call = [" + call + "], response = ["
                                + response + "]");
                        mProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            OxfordModel oxfordModel = response.body();
                            if (oxfordModel != null) {
                                if (oxfordModel.results.size() > 0) {
                                    parseDictionaryData(
                                            oxfordModel.results.get(0).lexicalEntries);
                                }
                            } else {
                                Toast.makeText(mContext, "Server error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.code() == 404) {
                            Toast.makeText(mContext, "Word not found",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Server error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OxfordModel> call, Throwable t) {
                        t.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void parseDictionaryData(List<LexicalEntry> results) {
        if (results != null) {
            mMeaningList.clear();
            for (LexicalEntry lexicalEntry : results) {
                MeaningModel meaningModel = new MeaningModel();
                meaningModel.setLexicalCategory(lexicalEntry.lexicalCategory);
                if (lexicalEntry.entries != null && lexicalEntry.entries.size() > 0) {
                    meaningModel.setSenseList(lexicalEntry.entries.get(0).senses);
                }
                if (lexicalEntry.pronunciations != null && lexicalEntry.pronunciations.size() > 0) {
                    meaningModel.setAudioFile(lexicalEntry.pronunciations.get(0).audioFile);
                    meaningModel.setPhoneticWord(
                            lexicalEntry.pronunciations.get(0).phoneticSpelling);
                }
                mMeaningList.add(meaningModel);
            }
            mMeaningAdapter.notifyDataSetChanged();
        }
    }
}
