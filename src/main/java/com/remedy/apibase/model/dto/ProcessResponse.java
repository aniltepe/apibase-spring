package com.remedy.apibase.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessResponse {
	@JsonAlias({"ObjectID"})
	public String processId;

	@JsonAlias({"Response"})
	public String processResponse;
}