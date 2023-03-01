package com.remedy.apibase.service;

import com.remedy.apibase.model.dto.Process;
import com.remedy.apibase.model.dto.ProcessStep;
import com.remedy.apibase.model.dto.Step;

public interface RemedyService {
    public Step getStepDetails(ProcessStep processStep) throws Exception;
	public Step createProcess(Process process) throws Exception;
}