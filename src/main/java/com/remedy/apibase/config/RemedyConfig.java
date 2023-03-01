package com.remedy.apibase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "remedy")
public class RemedyConfig {
    private String url;
    private String username;
    private String password;
    private String stepForm;
	private String kvForm;
	private String processForm;
	private String pstepForm;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStepForm(String stepForm) {
		this.stepForm = stepForm;
	}

	public void setKvForm(String kvForm) {
		this.kvForm = kvForm;
	}

	public void setProcessForm(String processForm) {
		this.processForm = processForm;
	}

	public void setPstepForm(String pstepForm) {
		this.pstepForm = pstepForm;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getStepForm() {
		return stepForm;
	}

	public String getKvForm() {
		return kvForm;
	}

	public String getProcessForm() {
		return processForm;
	}

	public String getPstepForm() {
		return pstepForm;
	}
}