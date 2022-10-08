package com.remedy.apibase.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomProcess {
    public String processId;
    public String configId;
}