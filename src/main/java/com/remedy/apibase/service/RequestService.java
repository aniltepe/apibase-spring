package com.remedy.apibase.service;

import com.remedy.apibase.model.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class RequestService {
	private static final Logger log = LoggerFactory.getLogger(RemedyService.class);
    private WebClient webClient = null;
    public RequestService(WebClient.Builder webClientBuilder) throws SSLException {
        SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
        HttpClient httpClient = HttpClient.create().secure(ssl -> {ssl.sslContext(sslContext);});
        webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    public <T> T ExecuteRequest(ParameterizedTypeReference<T> type, Config config) throws URISyntaxException, UnsupportedEncodingException {
        String url = config.endpoint;
        String paramStr = "";
        if (config.queryParams != null) {
            for (int i = 0; i < config.queryParams.size(); i++) {
                ConfigParam param = config.queryParams.get(i).values;
                paramStr = String.format("%s%s=%s&", paramStr, param.paramKey, URLEncoder.encode(param.paramValue, StandardCharsets.UTF_8.toString()));
            }
            if (paramStr.length() > 0) {
                paramStr = paramStr.substring(0, paramStr.length - 1);
                url = url + "?" + paramStr;
            }
        }
        WebClient.RequestBodyUriSpec request = webClient.method(HttpMethod.valueOf(config.method));
        WebClient.RequestBodySpec requestWithUri = request.uri(new URI(url));
        MediaType contentType = null;
        if (config.headers != null) {
            for (int i = 0; i < config.headers.size(); i++) {
                ConfigParam param = config.headers.get(i).values;
                requestWithUri = requestWithUri.header(param.paramKey, param.paramValue);
                if (param.paramKey.equals("Content-Type")) {
                    contentType = MediaType.valueOf(param.paramValue);
                }
            }
        }
        WebClient.RequestHeadersSpec requestWithBody = null;
        if (config.formData != null && config.formData.size() > 0) {
            if (contentType == null) {
                requestWithUri = requestWithUri.contentType(MediaType.APPLICATION_FORM_URLENCODED);
                contentType = MediaType.APPLICATION_FORM_URLENCODED;
            }
            if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
                for (int i = 0; i < config.formData.size(); i++) {
                    bodyValues.add(config.formData.get(i).values.paramKey, config.formData.get(i).values.paramValue);
                }
                requestWithBody = requestWithUri.body(BodyInserters.fromFormData(bodyValues));
            }
            else if (contentType.equals(MediaType.MULTIPART_FORM_DATA)) {
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                // builder.part("file", multipartFile.getResource());
                requestWithBody = requestWithUri.body(BodyInserters.fromMultipartData(builder.build()));
            }
        }
        else {
            if (contentType == null) {
                requestWithUri = requestWithUri.contentType(MediaType.APPLICATION_JSON);
                contentType = MediaType.APPLICATION_JSON;
            }
            if (contentType.equals(MediaType.APPLICATION_JSON) && config.body != null) {
                requestWithBody = requestWithUri.body(BodyInserters.fromValue(config.body));
            }
        }
        if (requestWithBody == null) {
            return requestWithUri.retrieve().bodyToMono(type).block();
        }
        else {
            return requestWithBody.retrieve().bodyToMono(type).block();
        }
    }
}
