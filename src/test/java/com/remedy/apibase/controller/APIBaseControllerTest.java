package com.remedy.apibase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remedy.apibase.TestUtils;
import com.remedy.apibase.model.dto.ProcessStep;
import com.remedy.apibase.service.APIBaseService;

@WebMvcTest(controllers = APIBaseController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class APIBaseControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private APIBaseService apiBaseService;
	public ObjectMapper objectMapper = new ObjectMapper();

	private MockHttpServletResponse execute(ProcessStep processStep) throws Exception {
		String uri = "/api/execute/";
		return mvc
			.perform(post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(processStep).getBytes()))
			.andReturn()
			.getResponse();
	}

	@Test
	public void executeTest() throws Exception {
		when(apiBaseService.execute(any())).thenReturn(TestUtils.generateCustomResponse());
		MockHttpServletResponse response = execute(TestUtils.generateProcessStep());
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
}