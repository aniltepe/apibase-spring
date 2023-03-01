package com.remedy.apibase.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StepKeyValue {
    @JsonProperty("KeyValueType__c")
    public String type;

    @JsonProperty("Key__c")
    public String key;

    @JsonProperty("Value__c")
    public String value;

    public StepKeyValue() { }

    public StepKeyValue(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }
}