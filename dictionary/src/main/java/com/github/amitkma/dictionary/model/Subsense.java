package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class Subsense {

    @SerializedName("definitions")
    public List<String> definitions;

    @SerializedName("domains")
    public List<String> domains;

    @SerializedName("examples")
    public List<Example> examples;

    @SerializedName("id")
    public String id;

    @SerializedName("registers")
    public List<String> registers;

    @SerializedName("notes")
    public List<Note> notes;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
