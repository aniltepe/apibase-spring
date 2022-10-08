package com.remedy.apibase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.remedy.apibase.model.Config;
import com.remedy.apibase.service.RemedyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.remedy.apibase.service.RequestService;
import org.s1f4j.Logger;
import org.s1f4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org. springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org. springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@SpringBootApplication
@RestController
public class TestApplication {
	private static final Logger log = LoggerFactory.getLogger(APIBaseApplication.class);

	@Autowired
    RemedyService remedyService;

	@Autowired
    RequestService requestService;

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@PostMapping("/NewServiceRequest")
	public String NewServiceRequest(@RequestBody String reqBody) throws JsonProcessingException, URISyntaxException, UnsupportedEncodingException {
		log.info("new request has arrived");
		Config newReqConfig = remedyService.GetConfig(reqBody);
		ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
		String response = requestService.ExecuteRequest(typeRef, newReqConfig);
		return "{ \"response\": \"" + response + "\" }";
	}
}
