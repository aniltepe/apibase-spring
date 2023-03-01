package com.remedy.apibase.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class Process {
	public String ObjectStatus = "Inactive";
	public String ObjectID;
	@JsonProperty("configurationId")
	public String ConfigurationID;
	@JsonProperty("parameter1")
	public String Parameter1;
	@JsonProperty("parameter2")
	public String Parameter2;
	@JsonProperty("parameter3")
	public String Parameter3;
	@JsonProperty("parameter4")
	public String Parameter4;
	@JsonProperty("parameter5")
	public String Parameter5;
	@JsonProperty("parameter6")
	public String Parameter6;
	public String SourceIPAddress;
	public String SourceHostName;
	public String KaynakUygulama;
	public String KaynakKullanici;

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("ObjectStatus", ObjectStatus);
		obj.put("ObjectID", ObjectID);
		obj.put("ConfigurationID", ConfigurationID);
		obj.put("Parameter1", Parameter1);
		obj.put("Parameter2", Parameter2);
		obj.put("Parameter3", Parameter3);
		obj.put("Parameter4", Parameter4);
		obj.put("Parameter5", Parameter5);
		obj.put("Parameter6", Parameter6);
		obj.put("SourceIPAddress", SourceIPAddress);
		obj.put("SourceHostName", SourceHostName);
		obj.put("KaynakUygulama", KaynakUygulama);
		obj.put("KaynakKullanici", KaynakKullanici);
		return obj;
	}
}