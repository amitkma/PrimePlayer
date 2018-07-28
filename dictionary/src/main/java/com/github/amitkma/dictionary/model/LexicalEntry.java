package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class LexicalEntry {

    @SerializedName("derivatives")
    public List<Derivative> derivatives;

    @SerializedName("entries")
    public List<Entry> entries;

    @SerializedName("language")
    public String language;

    @SerializedName("lexicalCategory")
    public String lexicalCategory;

    @SerializedName("pronunciations")
    public List<Pronunciation> pronunciations;

    @SerializedName("text")
    public String text;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
