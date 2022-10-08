package com.remedy.apibase.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigParam {
    @JsonProperty("ConfigurationRequestId__c")
    public String configId;

    @JsonAlias("ConfigurationHeadersKey__c", "ConfigurationQueryParamsKey__c", "ConfigurationFormDataKey__c")
    public String paramKey;

    @JsonAlias("ConfigurationHeadersValue__c", "ConfigurationQueryParamsValue__c", "ConfigurationFormDataValue__c")
    public String paramValue;

    public ConfigParam() {
        
    }

    public ConfigParam(String configId, String paramKey, String paramValue) {
        this.configId = configId;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }
}