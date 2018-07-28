package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */
public class Pronunciation {

    @SerializedName("audioFile")
    public String audioFile;

    @SerializedName("dialects")
    public List<String> dialects;

    @SerializedName("phoneticNotation")
    public String phoneticNotation;

    @SerializedName("phoneticSpelling")
    public String phoneticSpelling;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
