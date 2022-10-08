package com.remedy.apibase.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "remedy")
public class RemedyProperties {
    private String url;
    private String username;
    private String password;
    private String configForm;
    private String configHeadersForm;
    private String configQueryParamForm;
    private String configFormDataForm;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfigForm() { return configForm; }
    public void setConfigForm(String configForm) { this.configForm = configForm; }

    public String getConfigHeadersForm() { return configHeadersForm; }
    public void setConfigHeadersForm(String configHeadersForm) { this.configHeadersForm = configHeadersForm; }

    public String getConfigQueryParamForm() { return configQueryParamForm; }
    public void setConfigQueryParamForm(String configQueryParamForm) { this.configQueryParamForm = configQueryParamForm; }

    public String getConfigFormDataForm() { return configFormDataForm; }
    public void setConfigFormDataForm(String configFormDataForm) { this.configFormDataForm = configFormDataForm; }
}