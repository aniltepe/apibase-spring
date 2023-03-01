package com.remedy.apibase.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import com.remedy.apibase.model.dto.*;
import com.remedy.apibase.model.dto.Process;
import com.remedy.apibase.service.APIBaseService;
import com.remedy.apibase.service.RemedyService;
import com.remedy.apibase.service.RequestService;
import com.enterprise.log.logger.PlatformLogger;
import com.enterprise.log.logger.PlatformLoggerFactory;

@Service
public class APIBaseServiceImpl implements APIBaseService {
	private static final PlatformLogger log = PlatformLoggerFactory.getLogger(APIBaseServiceImpl.class);
	@Autowired
	RequestService requestService;

	@Autowired
	RemedyService remedyService;

	public ProcessStepResponse<String> execute(ProcessStep processStep) throws Exception {
		Step operationStep = remedyService.getStepDetails(processStep);
		ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
		return requestService.executeStep(typeRef, operationStep);
	}

	public ProcessResponse create(Process process) throws Exception {
		Step creationStep = remedyService.createProcess(process);
		ParameterizedTypeReference<RemedyEntry<ProcessResponse>> typeRef = new ParameterizedTypeReference<RemedyEntry<ProcessResponse>>() {};
		return requestService.executeStep(typeRef, creationStep).response.getValues();
	}
}