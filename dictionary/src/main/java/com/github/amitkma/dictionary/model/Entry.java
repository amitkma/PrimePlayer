package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class Entry {

    @SerializedName("etymologies")
    public List<String> etymologies;

    @SerializedName("grammaticalFeatures")
    public List<GrammaticalFeature> grammaticalFeatures;

    @SerializedName("homographNumber")
    public String homographNumber;

    @SerializedName("senses")
    public List<Sense> senses;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
