package com.remedy.apibase.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.StringUtils;
import com.remedy.apibase.TestUtils;
import com.remedy.apibase.model.dto.ProcessStepResponse;
import com.remedy.apibase.model.dto.Step;

@SpringBootTest
public class RequestServiceTest {
	@Autowired
	private RequestService requestService;

	@Test
	public void executeRequestTest() throws Exception {
		ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
		Step in = TestUtils.generateStep();
		ProcessStepResponse<String> out = requestService.executeStep(typeRef, in);
		Assertions.assertTrue(StringUtils.startsWithIgnoreCase(out.responseCode, "20"));
	}
}
