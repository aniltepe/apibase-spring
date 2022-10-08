package com.remedy.apibase.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemedyEntries<T> {
    @JsonProperty("entries")
    public List<RemedyEntry<T>> entries;
}