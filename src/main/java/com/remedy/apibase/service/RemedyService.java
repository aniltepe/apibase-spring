package com.remedy.apibase.service;

import com.remedy.apibase.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemedyService {
	private static final Logger log = LoggerFactory.getLogger(RemedyService.class);
    private WebClient webClient = null;
    private RemedyProperties remedy;

    @Autowired
    RequestService requestService;

    @Autowired
    public void setProperties(RemedyProperties props) { remedy = props; }

    public RemedyService(WebClient.Builder webClientBuilder) throws SSLException {
        SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
        HttpClient httpClient = HttpClient.create().secure(ssl -> {ssl.sslContext(sslContext);});
        webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    private String GetToken() throws  URISyntaxException, UnsupportedEncodingException {
        List<RemedyEntry<ConfigParam>> bodyForm = new ArrayList<RemedyEntry<ConfigParam>>();
        RemedyEntry<ConfigParam> param1entry = new RemedyEntry<ConfigParam>();
        ConfigParam param1 = new ConfigParam("", "username", remedy.getUsername());
        param1entry.values = param1;
        bodyForm.add(param1entry);
        RemedyEntry<ConfigParam> param2entry = new RemedyEntry<ConfigParam>();
        ConfigParam param2 = new ConfigParam("", "password", remedy.getPassword());
        param2entry.values = param2;
        bodyForm.add(param2entry);
        Config reqConfig = new Config();
        reqConfig.endpoint = String.format("%s/api/jwt/login", remedy.getUrl());
        reqConfig.method = "POST";
        reqConfig.formData = bodyForm;
        ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() { };
        return requestService.ExecuteRequest(typeRef, reqConfig);
    }

    public Config GetConfig(String reqBody) throws JsonProcessingException, URISyntaxException, UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        CustomProcess process = mapper.readValue(reqBody, CustomProcess.class);
        String token = GetToken();

        List<RemedyEntry<ConfigParam>> headers = new ArrayList<RemedyEntry<ConfigParam>>();
        RemedyEntry<ConfigParam> param1entry = new RemedyEntry<ConfigParam>();
        ConfigParam param1 = new ConfigParam("", "Authorization", String.format("AR-JWT %s", token));
        param1entry.values = param1;
        headers.add(param1entry);
        List<RemedyEntry<ConfigParam>> queryParams = new ArrayList<RemedyEntry<ConfigParam>>();
        RemedyEntry<ConfigParam> param2entry = new RemedyEntry<ConfigParam>();
        ConfigParam param2 = new ConfigParam("", "q", String.format("'ConfigurationRequestId__c'=\"%s\"", process.configId));
        param2entry.values = param2;
        queryParams.add(param2entry);

        ParameterizedTypeReference<RemedyEntry<Config>> configTypeRef = new ParameterizedTypeReference<RemedyEntry<Config>>(){};
        ParameterizedTypeReference<RemedyEntries<ConfigParam>> paramsTypeRef = new ParameterizedTypeReference<RemedyEntries<ConfigParam>>(){};

        Config reqConfig = new Config();
        reqConfig.endpoint = String.format("%s/api/arsys/v1/entry/%s/%s", remedy.getUrl(), remedy.getConfigForm(), process.configId);
        reqConfig.method = "GET";
        reqConfig.headers = headers;
        RemedyEntry<Config> newReqConfigEntry = requestService.ExecuteRequest(configTypeRef, reqConfig);
        Config returnConfig = newReqConfigEntry.values;

        reqConfig.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedy.getUrl(), remedy.getConfigHeadersForm());
        reqConfig.queryParams = queryParams;
        RemedyEntries<ConfigParam> responseConfigHeaders = requestService.ExecuteRequest(paramsTypeRef, reqConfig);
        returnConfig.headers = responseConfigHeaders.entries;

        reqConfig.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedy.getUrl(), remedy.getConfigQueryParamForm());
        RemedyEntries<ConfigParam> responseConfigQueryParams = requestService.ExecuteRequest(paramsTypeRef, reqConfig);
        returnConfig.queryParams = responseConfigQueryParams.entries;

        reqConfig.endpoint = String.format("%s/api/arsys/v1/entry/%s", remedy.getUrl(), remedy.getConfigFormDataForm());
        RemedyEntries<ConfigParam> responseConfigFormData = requestService.ExecuteRequest(paramsTypeRef, reqConfig);
        returnConfig.formData = responseConfigFormData.entries;

        return returnConfig;
    }
}
