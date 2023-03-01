package com.remedy.apibase.service;

import org.springframework.core.ParameterizedTypeReference;
import com.remedy.apibase.model.dto.ProcessStepResponse;
import com.remedy.apibase.model.dto.Step;

public interface RequestService {
	public static final String placeholderStart = "(((";
	public static final String placeholderEnd = ")))";
    public <T> ProcessStepResponse<T> executeStep(ParameterizedTypeReference<T> type, Step step)
            throws Exception;
}