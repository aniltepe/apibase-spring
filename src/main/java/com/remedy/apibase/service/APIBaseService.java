package com.remedy.apibase.service;

import com.remedy.apibase.model.dto.ProcessStepResponse;
import com.remedy.apibase.model.dto.Process;
import com.remedy.apibase.model.dto.ProcessResponse;
import com.remedy.apibase.model.dto.ProcessStep;

public interface APIBaseService {
	public ProcessStepResponse<String> execute(ProcessStep processStep) throws Exception;
	public ProcessResponse create(Process process) throws Exception;
}