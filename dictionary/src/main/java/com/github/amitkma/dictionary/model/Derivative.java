package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class Derivative {

    @SerializedName("id")
    public String id;

    @SerializedName("text")
    public String text;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
