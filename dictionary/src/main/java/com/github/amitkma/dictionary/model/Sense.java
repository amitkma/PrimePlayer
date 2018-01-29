package com.github.amitkma.dictionary.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by falcon on 27/1/18.
 */

public class Sense {

    @SerializedName("definitions")
    public List<String> definitions;

    @SerializedName("examples")
    public List<Example> examples;

    @SerializedName("id")
    public String id;

    @SerializedName("notes")
    public List<Note> notes;

    @SerializedName("domains")
    public List<String> domains;

    @SerializedName("subsenses")
    public List<Subsense> subsenses;

    @SerializedName("regions")
    public List<String> regions;

    @SerializedName("registers")
    public List<String> registers;

    @SerializedName("variantForms")
    public List<VariantForm> variantForms;

    public transient Map<String, Object> additionalProperties = new HashMap<>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
