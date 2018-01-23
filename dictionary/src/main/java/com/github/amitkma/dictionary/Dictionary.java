package com.github.amitkma.dictionary;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kuldeep on 19/1/18.
 */

public class Dictionary extends Service {
    private WindowManager mWindowManager;
    public View mFloatingView;
    public ScrollView mScrollView;
    public TextView mTextView;
    EditText mEditText;
    public FloatingActionButton mFab;
    ImageView mImageButton;

    public Dictionary() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //@Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_dictionary, null);

        mTextView = mFloatingView.findViewById(R.id.textView);
        mEditText = mFloatingView.findViewById(R.id.editText);
        mImageButton = mFloatingView.findViewById(R.id.button);
        mScrollView = mFloatingView.findViewById(R.id.scrollView3);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                350, 500,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP
                | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 125;
        params.y = 100 + 30;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().matches("")) {
                    Toast.makeText(mFloatingView.getContext(), "no word to search",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new CallbackTask().execute(inflections());
                    mTextView.setText("Searching");
                    mTextView.setBackgroundColor(Color.WHITE);
                    mScrollView.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }


    public String inflections() {
        final String language = "en";

        final String word = mEditText.getText().toString().trim();
        final String word_id =
                word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/"
                + word_id;
    }


    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, ArrayList<DictionaryWord>> {
        DictionaryWord d1;
        public MediaPlayer mediaPlayer = new MediaPlayer();

        @Override
        protected ArrayList<DictionaryWord> doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "adc0285b";
            final String app_key = "8135a43940bb5877b79b8c127bc1c99c";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", app_id);
                urlConnection.setRequestProperty("app_key", app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                urlConnection.disconnect();
                /////json parse

                String def = "eror";
                ArrayList<DictionaryWord> dictinoryData = new ArrayList<>();
                try {
                    JSONObject js = new JSONObject(stringBuilder.toString());
                    JSONArray results = js.getJSONArray("results");

                    JSONObject lentries = results.getJSONObject(0);
                    //add
                    String id = (String) lentries.get("id");
                    JSONArray la = lentries.getJSONArray("lexicalEntries");

                    JSONObject entries = la.getJSONObject(0);
                    //retriving url
                    JSONArray urls = entries.getJSONArray("pronunciations");

                    JSONObject get_url = urls.getJSONObject(0);
                    String audio_url = get_url.getString("audioFile");

                    String Deriv = "bhai ja";

                 /*  JSONArray derivate = entries.getJSONArray("derivatives");
                    JSONObject jobjDer= derivate.getJSONObject(0);
                    //add
                    String Deriv = jobjDer.getString("text");*/

                    JSONArray e = entries.getJSONArray("entries");

                    JSONObject senses = e.getJSONObject(0);
                    JSONArray s = senses.getJSONArray("senses");//s==j4
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject d = s.getJSONObject(i);
                        JSONArray de = d.getJSONArray("definitions");

                        String mean = de.getString(0);

                        try {
                            JSONArray examp = d.getJSONArray("examples");
                            ArrayList<String> expary = new ArrayList<>();
                            for (int j = 0; j < examp.length(); j++) {
                                JSONObject text = examp.getJSONObject(j);
                                expary.add(text.getString("text"));

                            }
                            dictinoryData.add(
                                    new DictionaryWord(id, Deriv, mean, expary, audio_url));
                        } catch (Exception e1) {
                            Log.d(
                                    "Main_activity", "no examples");

                            dictinoryData.add(new DictionaryWord(id, Deriv, mean, audio_url));
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    ArrayList<DictionaryWord> ki = new ArrayList<DictionaryWord>();
                    return ki;
                }
                return dictinoryData;

            } catch (Exception e) {
                e.printStackTrace();
                ArrayList<DictionaryWord> ki = new ArrayList<>();
                return ki;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<DictionaryWord> arrayList) {
            super.onPostExecute(arrayList);

            if (arrayList.size() != 0) {
                String s = "";
                ArrayList<String> exple;
                for (int i = 0; i < arrayList.size(); i++) {
                    s = s + "Definition" + "\n" + arrayList.get(i).getMeanWrd() + "\n\n";

                    exple = arrayList.get(i).getExamples();
                    if (exple != null) {
                        s = s + "Examples" + "\n";
                        for (int j = 0; j < exple.size(); j++) {
                            s = s + exple.get(j) + "\n\n";
                        }
                    }
                    s = s + "\n\n";

                }


                mTextView.setText(s);
                mTextView.setBackgroundColor(getResources().getColor(R.color.onfind));
                mScrollView.setBackgroundColor(getResources().getColor(R.color.onfind));
                d1 = arrayList.get(0);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(d1.getAudioUrl());
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mFab = mFloatingView.findViewById(R.id.floatingActionButton);
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();

                    }
                });


            } else {
                mTextView.setText("Word not found");
                mTextView.setBackgroundColor(getResources().getColor(R.color.errorcode));
                mScrollView.setBackgroundColor(getResources().getColor(R.color.errorcode));
                mFab = mFloatingView.findViewById(R.id.floatingActionButton);
                mFab.setOnClickListener(null);
            }

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }


}
