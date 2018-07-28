package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class Result {
    @SerializedName("id")
    public String id;

    @SerializedName("language")
    public String language;

    @SerializedName("lexicalEntries")
    public List<LexicalEntry> lexicalEntries;

    @SerializedName("type")
    public String type;

    @SerializedName("word")
    public String word;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
