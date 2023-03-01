package com.remedy.apibase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.remedy.apibase.model.dto.ProcessStepResponse;
import com.remedy.apibase.model.dto.Process;
import com.remedy.apibase.model.dto.ProcessResponse;
import com.remedy.apibase.model.dto.ProcessStep;
import com.remedy.apibase.service.APIBaseService;
import com.enterprise.log.logger.PlatformLogger;
import com.enterprise.log.logger.PlatformLoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "apibase-controller", description = "Remedyden veya disaridan gelen istegin handle edildigi controllerdir")
public class APIBaseController {
	private static final PlatformLogger log = PlatformLoggerFactory.getLogger(APIBaseController.class);
	@Autowired
	APIBaseService apiBaseService;

	@PostMapping(value = "/execute")
	@Operation(summary = "Remedyden gelen islem adimlarini yani operasyonlari calistirir")
	public ResponseEntity<ProcessStepResponse<String>> execute(@RequestBody ProcessStep processStep, HttpServletRequest request) throws Exception {
		log.info("APIBaseController.execute", "input: {} ", processStep.toString());
		return ResponseEntity.ok(apiBaseService.execute(processStep));
	}

	@PostMapping(value = "/create")
	@Operation(summary = "Remedyde yeni islem olusturarak islem adimlarinin execute edilmesini saglar")
	public ResponseEntity<ProcessResponse> create(@RequestBody Process process, @RequestHeader("Kaynak-Uygulama") String kaynakUygulama,
												  @RequestHeader("Kaynak-Kullanici") String kaynakKullanici, HttpServletRequest request)
		throws Exception {
		log.info("ProcessController.create", "input: {} ", process.toString());
		String ipAddress = request.getRemoteAddr();
		InetAddress inetAddress = InetAddress.getByName(ipAddress);
		String hostName = inetAddress.getHostName();
		process.SourceIPAddress = ipAddress;
		process.SourceHostName = hostName;
		process.KaynakUygulama = kaynakUygulama;
		process.KaynakKullanici = kaynakKullanici;

		return ResponseEntity.ok(apiBaseService.create(process));
	}
}