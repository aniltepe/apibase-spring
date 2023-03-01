package com.remedy.apibase.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;
import com.remedy.apibase.TestUtils;
import com.remedy.apibase.model.dto.ProcessStep;
import com.remedy.apibase.model.dto.Step;

@SpringBootTest
public class RemedyServiceTest {
	@Autowired
	private RemedyService remedyService;

	@Test
	public void getConfigTest() throws Exception {
		ProcessStep in = TestUtils.generateProcessStep_Placeholder();
		Step out = remedyService.getStepDetails(in);
		Assertions.assertTrue(out != null);
		Assertions.assertTrue(StringUtils.hasText(out.endpoint));
		Assertions.assertTrue(StringUtils.hasText(out.method));
	}
}