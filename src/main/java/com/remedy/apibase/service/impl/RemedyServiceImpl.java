package com.remedy.apibase.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.*;
import com.remedy.apibase.config.RemedyConfig;
import com.remedy.apibase.model.dto.*;
import com.remedy.apibase.model.dto.Process;
import com.remedy.apibase.service.RemedyService;
import com.remedy.apibase.service.RequestService;
import com.enterprise.log.logger.PlatformLogger;
import com.enterprise.log.logger.PlatformLoggerFactory;

@Service
public class RemedyServiceImpl implements RemedyService {
    private static final PlatformLogger log = PlatformLoggerFactory.getLogger(RemedyServiceImpl.class);

	@Autowired
    private RemedyConfig remedyConfig;

    @Autowired
	private RequestService requestService;

    private String getToken() throws Exception {
		Step tokenStep = new Step();
		tokenStep.endpoint = String.format("%s/api/jwt/login", remedyConfig.getUrl());
		tokenStep.method = "POST";
		tokenStep.formData.add(new StepKeyValue("FormData", "username", remedyConfig.getUsername()));
		tokenStep.formData.add(new StepKeyValue("FormData", "password", remedyConfig.getPassword()));
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>(){};
		ProcessStepResponse<String> resp = requestService.executeStep(typeRef, tokenStep);
        return resp.response;
    }

    public Step getStepDetails(ProcessStep processStep) throws Exception {
		String token = getToken();

        Step reqStep = new Step();
		reqStep.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedyConfig.getUrl(), remedyConfig.getStepForm());
		reqStep.method = "GET";
		reqStep.headers.add(new StepKeyValue("Header", "Authorization", String.format("AR-JWT %s", token)));
		reqStep.queryParams.add(new StepKeyValue("QueryParam", "q", String.format("'ObjectID'=\"%s\"", processStep.stepId)));
		ParameterizedTypeReference<RemedyEntries<Step>> stepTypeRef = new ParameterizedTypeReference<RemedyEntries<Step>>(){};
		RemedyEntries<Step> newReqStepEntries = requestService.executeStep(stepTypeRef, reqStep).response;
		Step returnStep = newReqStepEntries.entries.stream().map(RemedyEntry<Step>::getValues).collect(Collectors.toList()).get(0);
		reqStep.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedyConfig.getUrl(), remedyConfig.getKvForm());
		reqStep.queryParams.clear();
		reqStep.queryParams.add(new StepKeyValue("QueryParam", "q", String.format("'SourceID__c'=\"%s\" AND 'ObjectStatus__c'=\"Active\"", processStep.stepId)));
		ParameterizedTypeReference<RemedyEntries<StepKeyValue>> kvTypeRef = new ParameterizedTypeReference<RemedyEntries<StepKeyValue>>(){};
        RemedyEntries<StepKeyValue> newReqStepEntryKVs = requestService.executeStep(kvTypeRef, reqStep).response;
        List<StepKeyValue> stepKVs = newReqStepEntryKVs.entries.stream().map(RemedyEntry<StepKeyValue>::getValues).collect(Collectors.toList());
		for (int i = 0; i < stepKVs.size(); i++) {
			String type = stepKVs.get(i).type;
			if (type.equals("Header"))
				returnStep.headers.add(stepKVs.get(i));
			else if (type.equals("QueryParam"))
				returnStep.queryParams.add(stepKVs.get(i));
			else if (type.equals("FormData"))
				returnStep.formData.add(stepKVs.get(i));
		}

        return checkPlaceholder(processStep, returnStep);
    }

    private String replacePlaceholder(String field, ProcessStep processStep) throws Exception {
        String placeholderStart = requestService.placeholderStart;
        String placeholderEnd = requestService.placeholderEnd;
        String part1 = field.split(Pattern.quote(placeholderStart))[1];
        String part2 = part1.split(Pattern.quote(placeholderEnd))[0];
        if (part2.startsWith("O")) {
			//replacing placeholder with previous response
            String queryOrder = part2.split(":")[1];

            String token = getToken();
            Step reqStep = new Step();
			reqStep.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedyConfig.getUrl(), remedyConfig.getPstepForm());
			reqStep.method = "GET";
			reqStep.headers.add(new StepKeyValue("Header", "Authorization", String.format("AR-JWT %s", token)));
			reqStep.queryParams.add(new StepKeyValue("QueryParam", "q", String.format("'ProcessID'=\"%s\" AND 'Order'=\"%s\"", processStep.processId, queryOrder)));
            ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
            String prevStep = requestService.executeStep(typeRef, reqStep).response;
            String prevStepResp = new JSONObject(prevStep).getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("FormattedResponse");
            field = field.replace(placeholderStart + part2 + placeholderEnd, prevStepResp);
        }
		else if (part2.equals("RN")) {
			//replacing placeholder with request number
			field = field.replace(placeholderStart + part2 + placeholderEnd, processStep.reqNumber);
		}
		else if (part2.equals("AN")) {
			//replacing placeholder with request app number
			field = field.replace(placeholderStart + part2 + placeholderEnd, processStep.reqAppNumber);
		}
        else if (part2.startsWith("P")) {
            int queryParameter = Integer.parseInt(part2.split(":")[1]);
            String parameterValue = "";
            switch (queryParameter) {
                case 1:
					parameterValue = processStep.parameter1;
                    break;
                case 2:
					parameterValue = processStep.parameter2;
                    break;
                case 3:
					parameterValue = processStep.parameter3;
                    break;
                case 4:
					parameterValue = processStep.parameter4;
                    break;
                case 5:
					parameterValue = processStep.parameter5;
                    break;
                case 6:
					parameterValue = processStep.parameter6;
                    break;
            }
            field = field.replace(placeholderStart + part2 + placeholderEnd, parameterValue);
        }
        return field;
    }

    private Step checkPlaceholder(ProcessStep processStep, Step step) throws Exception {
        String placeholderStart = requestService.placeholderStart;
        String placeholderEnd = requestService.placeholderEnd;
        if (step.endpoint != null && step.endpoint.contains(placeholderStart) && step.endpoint.contains(placeholderEnd)) {
			step.endpoint = replacePlaceholder(step.endpoint, processStep);
        }
        if (step.body != null && step.body.contains(placeholderStart) && step.body.contains(placeholderEnd)) {
			step.body = replacePlaceholder(step.body, processStep);
        }
        if (step.responseFormat != null && step.responseFormat.contains(placeholderStart) && step.responseFormat.contains(placeholderEnd)) {
			step.responseFormat = replacePlaceholder(step.responseFormat, processStep);
        }
        if (step.headers != null) {
            for (int i = 0; i < step.headers.size(); i++) {
                if (step.headers.get(i).key.contains(placeholderStart) && step.headers.get(i).key.contains(placeholderEnd)) {
					step.headers.get(i).key = replacePlaceholder(step.headers.get(i).key, processStep);
                }
                if (step.headers.get(i).value.contains(placeholderStart) && step.headers.get(i).value.contains(placeholderEnd)) {
					step.headers.get(i).value = replacePlaceholder(step.headers.get(i).value, processStep);
                }
            }
        }
        if (step.queryParams != null) {
            for (int i = 0; i < step.queryParams.size(); i++) {
                if (step.queryParams.get(i).key.contains(placeholderStart) && step.queryParams.get(i).key.contains(placeholderEnd)) {
					step.queryParams.get(i).key = replacePlaceholder(step.queryParams.get(i).key, processStep);
                }
                if (step.queryParams.get(i).value.contains(placeholderStart) && step.queryParams.get(i).value.contains(placeholderEnd)) {
					step.queryParams.get(i).value = replacePlaceholder(step.queryParams.get(i).value, processStep);
                }
            }
        }
        if (step.formData != null) {
            for (int i = 0; i < step.formData.size(); i++) {
                if (step.formData.get(i).key.contains(placeholderStart) && step.formData.get(i).key.contains(placeholderEnd)) {
					step.formData.get(i).key = replacePlaceholder(step.formData.get(i).key, processStep);
                }
                if (step.formData.get(i).value.contains(placeholderStart) && step.formData.get(i).value.contains(placeholderEnd)) {
					step.formData.get(i).value = replacePlaceholder(step.formData.get(i).value, processStep);
                }
            }
        }

        return step;
    }
}