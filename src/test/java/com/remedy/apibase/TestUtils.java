package com.remedy.apibase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import com.remedy.apibase.model.dto.*;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static ObjectMapper mapper = new ObjectMapper();

    public static Long randomLong() { return RandomUtils.nextLong(); }

    public static String randomString(int length) { return RandomStringUtils.randomAlphanumeric(length); }

    public static ProcessStep generateProcessStep() {
        ProcessStep processStep = new ProcessStep();
        processStep.processId = "PRGAA5V0H3OHOARQU09ORPU238VCUD";
        processStep.stepId = "STGAA5V0H3OHOARQUH9ARPUJ2VUTA2";
        processStep.reqNumber = "REQ000001";
        processStep.reqAppNumber = "WO000001";
        processStep.parameter1 = "parameter1";
        processStep.parameter2 = "parameter2";
        processStep.parameter3 = "parameter3";
        processStep.parameter4 = "parameter4";
        processStep.parameter5 = "parameter5";
        processStep.parameter6 = "parameter6";
        return processStep;
    }

    public static ProcessStep generateProcessStep_Placeholder() {
        ProcessStep processStep = new ProcessStep();
        processStep.processId = "PRGAA5V0H3OHOARQU09ORPU238VCUD";
        processStep.stepId = "STGAA5V0H3OHOARQUH9ARPUJ2VUTA3";
        processStep.reqNumber = "REQ000001";
        processStep.reqAppNumber = "WO000001";
        processStep.parameter1 = "CRQ00001";
        processStep.parameter2 = "parameter2";
        processStep.parameter3 = "parameter3";
        processStep.parameter4 = "parameter4";
        processStep.parameter5 = "parameter5";
        processStep.parameter6 = "parameter6";
        return processStep;
    }

    public static ProcessStepResponse<String> generateProcessStepResponse() {
        ProcessStepResponse<String> processStepResponse = new ProcessStepResponse<String>();
        processStepResponse.response = "successful";
        processStepResponse.formattedResponse = "successful";
        processStepResponse.responseCode = "200";
        return processStepResponse;
    }

    public static Step generateStep() {
        List<StepKeyValue> bodyForm = new ArrayList<StepKeyValue>();
        StepKeyValue kv1 = new StepKeyValue("FormData", "username", "xxx");
        bodyForm.add(kv1);
        StepKeyValue kv2 = new StepKeyValue("FormData", "password", "???");
        bodyForm.add(kv2);
        Step step = new Step();
        step.endpoint = "https://remedytest.com/api/jwt/login";
        step.method = "POST";
        step.formData = bodyForm;
        return step;
    }
}