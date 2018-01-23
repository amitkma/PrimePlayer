package com.github.amitkma.dictionary;

import java.util.ArrayList;

/**
 * Created by kuldeep on 17/1/18.
 */

public class DictionaryWord {
    private String mWord, mDerivativeOfWrd, mMeanWrd, mAudioUrl;
    private ArrayList<String> examples;

    public DictionaryWord(String word, String derivativeWord, String meanWord,
            ArrayList<String> examples, String audioUrl) {
        mWord = word;
        mDerivativeOfWrd = derivativeWord;
        mMeanWrd = meanWord;
        mAudioUrl = audioUrl;
        this.examples = examples;
    }

    public DictionaryWord(String mword, String mderiv_of_wrd, String mmean_wrd, String maudio_url) {
        mWord = mword;
        mDerivativeOfWrd = mderiv_of_wrd;
        mMeanWrd = mmean_wrd;
        mAudioUrl = maudio_url;
        examples = null;
    }


    public String getDerivativeOfWrd() {
        return mDerivativeOfWrd;
    }

    public String getMeanWrd() {
        return mMeanWrd;
    }

    public String getWord() {
        return mWord;
    }

    public ArrayList<String> getExamples() {
        return examples;
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }
}
