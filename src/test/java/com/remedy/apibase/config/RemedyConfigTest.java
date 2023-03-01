package com.remedy.apibase.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import com.remedy.apibase.Application;
import com.remedy.apibase.service.APIBaseService;

@SpringBootTest
public class RemedyConfigTest {
	@Autowired
	private RemedyConfig remedyConfig;

	@Test
	void setUrl() {
		String url = "https://mock.com/";
		remedyConfig.setUrl(url);
		Assertions.assertTrue(remedyConfig.getUrl() == url);
	}
	@Test
	void setUsername() {
		String un = "testuser";
		remedyConfig.setUsername(un);
		Assertions.assertTrue(remedyConfig.getUsername() == un);
	}
	@Test
	void setPassword() {
		String pw = "???";
		remedyConfig.setPassword(pw);
		Assertions.assertTrue(remedyConfig.getPassword() == pw);
	}
	@Test
	void setStepForm() {
		String sf = "ApiBase:Step";
		remedyConfig.setStepForm(sf);
		Assertions.assertTrue(remedyConfig.getStepForm() == sf);
	}
	@Test
	void setKVForm() {
		String kvf = "ApiBase:Join:KeyValue";
		remedyConfig.setKvForm(kvf);
		Assertions.assertTrue(remedyConfig.getKvForm() == kvf);
	}
	@Test
	void setProcessForm() {
		String pf = "ApiBase:Process";
		remedyConfig.setProcessForm(pf);
		Assertions.assertTrue(remedyConfig.getProcessForm() == pf);
	}
	@Test
	void setPStepForm() {
		String psf = "ApiBase:ProcessStep";
		remedyConfig.setPstepForm(psf);
		Assertions.assertTrue(remedyConfig.getPstepForm() == psf);
	}
}