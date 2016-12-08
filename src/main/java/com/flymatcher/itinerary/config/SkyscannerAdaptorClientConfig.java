package com.flymatcher.itinerary.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SkyscannerAdaptorClientConfig {

  @Bean
  RestTemplate skyscannerClientRestTemplate(
      final ClientHttpRequestFactory scyscannerClientClientHttpRequestFactory,
      final ObjectMapper mapper) {
    // Seems like spring-boot does not auto-wire the spring MVC ObjectMapper into rest template so
    // we do this here
    final RestTemplate restTemplate = new RestTemplate(scyscannerClientClientHttpRequestFactory);
    final List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();

    messageConverters.add(0, new MappingJackson2HttpMessageConverter(mapper));

    return restTemplate;
  }

  @Bean
  public RetryPolicy scyscannerClientClientRetryPolicy() {
    return new NeverRetryPolicy();
  }


  @Bean
  public ClientHttpRequestFactory scyscannerClientClientHttpRequestFactory() {
    return new HttpComponentsClientHttpRequestFactory();

  }
}
