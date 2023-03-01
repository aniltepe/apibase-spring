package com.remedy.apibase.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    @JsonProperty("Method")
    public String method;

    @JsonProperty("Endpoint")
    public String endpoint;
    
    @JsonProperty("Body")
    public String body;

    public List<StepKeyValue> headers = new ArrayList<StepKeyValue>();
    public List<StepKeyValue> queryParams = new ArrayList<StepKeyValue>();
    public List<StepKeyValue> formData = new ArrayList<StepKeyValue>();

    @JsonProperty("ResponseType")
    public String responseType;

    @JsonProperty("ResponseHeader")
    public String responseHeader;
    
    @JsonProperty("ResponseFormat")
    public String responseFormat;
}