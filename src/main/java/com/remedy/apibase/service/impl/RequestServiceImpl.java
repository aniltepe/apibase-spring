package com.remedy.apibase.service.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.remedy.apibase.model.dto.*;
import com.remedy.apibase.service.RequestService;
import com.enterprise.log.logger.PlatformLogger;
import com.enterprise.log.logger.PlatformLoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class RequestServiceImpl implements RequestService {
    private static final PlatformLogger log = PlatformLoggerFactory.getLogger(RequestServiceImpl.class);
    public static final String placeholderStart = "(((";
    public static final String placeholderEnd = ")))";

	@Autowired
    private WebClient webClient;

    public <T> ProcessStepResponse<T> executeStep(ParameterizedTypeReference<T> type, Step step)
            throws Exception {
		if (step == null) {
			return null;
		}
		if (!StringUtils.hasText(step.endpoint) || !StringUtils.hasText(step.method)) {
			return null;
		}
		// log.info("executeStep", "executing step, {}, {}", step.method, step.endpoint);
        String url = step.endpoint;
        String paramStr = "";
		for (int i = 0; i < step.queryParams.size(); i++) {
			StepKeyValue kv = step.queryParams.get(i);
			paramStr = String.format("%s%s=%s&", paramStr, kv.key, URLEncoder.encode(kv.value, StandardCharsets.UTF_8.toString()));
		}
		if (paramStr.length() > 0) {
			paramStr = paramStr.substring(0, paramStr.length() - 1);
			url = url + "?" + paramStr;
		}
        WebClient.RequestBodyUriSpec request = webClient.method(HttpMethod.valueOf(step.method));
		WebClient.RequestBodySpec requestWithUri = null;
		if (url.matches("^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$")) {
			requestWithUri = request.uri(new URI(url));
		}
		else {
			throw new IllegalArgumentException();
		}

        MediaType contentType = null;
		for (int i = 0; i < step.headers.size(); i++) {
			StepKeyValue kv = step.headers.get(i);
			String headerVal = org.apache.commons.lang3.StringUtils.normalizeSpace(kv.value);
			if (kv.key.matches("^[a-zA-Z][a-zA-Z\\d-]*$") && headerVal.equals(sanitizeString(headerVal))) {
				requestWithUri = requestWithUri.header(kv.key, headerVal);
			}
			else {
				throw new IllegalArgumentException();
			}

			if (kv.key.equals("Content-Type"))
				contentType = MediaType.valueOf(kv.value);
		}
        WebClient.RequestHeadersSpec requestWithBody = null;
        if (step.formData.size() > 0) {
            if (contentType == null) {
                requestWithUri = requestWithUri.contentType(MediaType.APPLICATION_FORM_URLENCODED);
                contentType = MediaType.APPLICATION_FORM_URLENCODED;
            }
            if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
                for (int i = 0; i < step.formData.size(); i++)
                    bodyValues.add(step.formData.get(i).key, step.formData.get(i).value);
                requestWithBody = requestWithUri.body(BodyInserters.fromFormData(bodyValues));
            }
            else if (contentType.equals(MediaType.MULTIPART_FORM_DATA)) {
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
//                builder.part("file", multipartFile.getResource());
                requestWithBody = requestWithUri.body(BodyInserters.fromMultipartData(builder.build()));
            }
        }
        else {
            if (contentType == null) {
                requestWithUri = requestWithUri.contentType(MediaType.APPLICATION_JSON);
                contentType = MediaType.APPLICATION_JSON;
            }
            if ((contentType.equals(MediaType.APPLICATION_JSON) || contentType.equals(MediaType.APPLICATION_XML)) && step.body != null)
                requestWithBody = requestWithUri.body(BodyInserters.fromValue(step.body));
        }
        WebClient.ResponseSpec responseSpec = null;
        if (requestWithBody == null)
			responseSpec = requestWithUri.retrieve();
        else
			responseSpec = requestWithBody.retrieve();
		ResponseEntity<T> responseEntity = responseSpec.toEntity(type).block();
        ProcessStepResponse<T> processStepResponse = new ProcessStepResponse<T>();
        processStepResponse.responseCode = String.valueOf(responseEntity.getStatusCodeValue());
        processStepResponse.response = responseEntity.getBody();
        if (type.getType() != String.class)
			return processStepResponse;
        processStepResponse.formattedResponse = processStepResponse.response.toString();
        if (step.responseType == null || (step.responseType != null && step.responseType.equals("")))
			step.responseType = "BodyText";
        
        if (step.responseType.equals("BodyText")) {
            if (step.responseFormat == null || (step.responseFormat != null && step.responseFormat.equals("")))
                return processStepResponse;
            String[] parts = step.responseFormat.split(Pattern.quote(placeholderStart + placeholderEnd));
            for (int i = 0; i < parts.length; i++)
                processStepResponse.formattedResponse = processStepResponse.formattedResponse.replace(parts[i], "");
        }
        else if (step.responseType.equals("BodyObject")) {
            if (step.responseFormat == null || (step.responseFormat != null && step.responseFormat.equals("")))
                return processStepResponse;
            String[] parts = step.responseFormat.split(Pattern.quote("."));
            JSONObject currObj = new JSONObject(processStepResponse.formattedResponse);
            for (int i = 0; i < parts.length; i++) {
                if (i != parts.length - 1) {
                    if (parts[i].contains("[") && parts[i].contains("]")) {
                        int objIdx = Integer.valueOf(parts[i].split(Pattern.quote("["))[1].split(Pattern.quote("]"))[0]);
                        JSONArray tempArr = currObj.getJSONArray(parts[i].split(Pattern.quote("["))[0]);
                        currObj = tempArr.getJSONObject(objIdx);
                    }
                    else
                        currObj = currObj.getJSONObject(parts[i]);
                }
                else {
                    if (parts[i].contains("[") && parts[i].contains("]")) {
                        int objIdx = Integer.valueOf(parts[i].split(Pattern.quote("["))[1].split(Pattern.quote("]"))[0]);
                        JSONArray tempArr = currObj.getJSONArray(parts[i].split(Pattern.quote("["))[0]);
                        processStepResponse.formattedResponse = tempArr.getString(objIdx);
                    }
                    else
                        processStepResponse.formattedResponse = currObj.getString(parts[i]);
                }
            }
        }
        else if (step.responseType.equals("HeaderText")) {
            if (step.responseHeader == null || (step.responseHeader != null && step.responseHeader.equals("")))
                return processStepResponse;
			HttpHeaders responseHeaders = responseEntity.getHeaders();
            processStepResponse.formattedResponse = String.valueOf(responseHeaders.get(step.responseHeader));
            if (step.responseFormat == null || (step.responseFormat != null && step.responseFormat.equals("")))
                return processStepResponse;
            String[] parts = step.responseFormat.split(Pattern.quote(placeholderStart + placeholderEnd));
            for (int i = 0; i < parts.length; i++)
                processStepResponse.formattedResponse = processStepResponse.formattedResponse.replace(parts[i], "");
        }
        return processStepResponse;
    }

    private String sanitizeString(String value) {
		String cleanValue = null;
		if (value != null) {
			cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD);

			// Avoid null characters
			cleanValue = cleanValue.replaceAll("\0", "");

			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");
		}
		return cleanValue;
	}
}