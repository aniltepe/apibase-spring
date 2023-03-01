package com.remedy.apibase.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	public static final int TIMEOUT = 10000;

	@Bean
	public WebClient webClient(WebClient.Builder webClientBuilder) throws Exception {
		SslContext fakeSslContext = SslContextBuilder
			.forClient()
			.trustManager(InsecureTrustManagerFactory.INSTANCE)
			.build();

		final HttpClient httpClient = HttpClient.create()
			.secure(sslProviderBuilder -> {
				sslProviderBuilder.sslContext(fakeSslContext);
			})
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
			.responseTimeout(Duration.ofMillis(TIMEOUT))
			.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
				.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();

	}
}