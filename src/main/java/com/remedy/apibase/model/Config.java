package com.remedy.apibase.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    @JsonProperty("Request ID__c")
    public String requestId;

    @JsonProperty("ExecutionOrder")
    public int order;

    @JsonProperty("HTTPMethod")
    public String method;

    @JsonProperty("Endpoint")
    public String endpoint;
    
    @JsonProperty("Body__c")
    public String body;

    public List<HarmoniEntry<ConfigParam> headers;
    public List<HarmoniEntry<ConfigParam> queryParams;
    public List<HarmoniEntry<ConfigParam> formData;
}