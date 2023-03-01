package com.remedy.apibase.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;
import com.remedy.apibase.TestUtils;
import com.remedy.apibase.model.dto.ProcessStepResponse;
import com.remedy.apibase.model.dto.ProcessStep;

@SpringBootTest
public class APIBaseServiceTest {
	@Autowired
	private APIBaseService apiBaseService;

	@Test
	public void executeTest() throws Exception {
		ProcessStep in = TestUtils.generateProcessStep();
		ProcessStepResponse<String> out = apiBaseService.execute(in);
		Assertions.assertTrue(StringUtils.startsWithIgnoreCase(out.responseCode, "20"));
	}
}