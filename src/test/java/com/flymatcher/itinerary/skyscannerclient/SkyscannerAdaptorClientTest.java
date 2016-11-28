package com.flymatcher.itinerary.skyscannerclient;

import static com.flymatcher.skyscanner.adaptor.api.builders.CheapestQuotesRequestBuilder.aCheapestQuotesRequest;
import static com.flymatcher.skyscanner.adaptor.api.builders.SkyscannerCheapestQuotesResponseBuilder.aSkyscannerCheapestQuotesResponse;
import static java.nio.charset.Charset.forName;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flymatcher.itinerary.exception.SkyscannerAdaptorBadRequestException;
import com.flymatcher.itinerary.exception.SkyscannerAdaptorServerException;
import com.flymatcher.skyscanner.adaptor.api.CheapestQuotesRequest;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;


public class SkyscannerAdaptorClientTest {

  private static final String SKYSCANNER_ADAPTOR_BASE_URL = "base-url";
  private static final String ADAPTOR_CHEAPEST_QUOTES_URL = "/v1/cheapest-quotes/";

  private static final String CHEAPEST_QUOTES_URL =
      SKYSCANNER_ADAPTOR_BASE_URL + ADAPTOR_CHEAPEST_QUOTES_URL;

  private static final String ERROR_MESSAGE = "Could not get a valid skyscanner adaptor response.";
  private static final String VALIDATION_MESSAGE =
      "Skyscanner adaptor response included validation errors.";

  private SkyscannerAdaptorClient client;

  @Mock
  private RestTemplate mockRestTemplate;
  @Mock
  private ResponseEntity<SkyscannerCheapestQuotesResponse> mockResponseEntity;


  @Rule
  public ExpectedException expectedException = none();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    client = new SkyscannerAdaptorClientImpl(mockRestTemplate, SKYSCANNER_ADAPTOR_BASE_URL,
        new ObjectMapper());
  }

  @Test
  public void shouldGetCheapestQuotes() {

    final CheapestQuotesRequest cheapestQuotesRequest =
        aCheapestQuotesRequest().withDefaultValues().build();

    final SkyscannerCheapestQuotesResponse expected = aSkyscannerCheapestQuotesResponse().build();

    given(mockRestTemplate.exchange(CHEAPEST_QUOTES_URL, GET,
        new HttpEntity<CheapestQuotesRequest>(cheapestQuotesRequest),
        SkyscannerCheapestQuotesResponse.class)).willReturn(mockResponseEntity);

    given(mockResponseEntity.getBody()).willReturn(expected);

    given(mockResponseEntity.getStatusCode()).willReturn(OK);

    final SkyscannerCheapestQuotesResponse actual = client.getCheapestQuotes(cheapestQuotesRequest);

    assertEquals(expected, actual);

  }

  @Test
  public void shouldThrowSkyscannerBadRequestFor400() {

    final CheapestQuotesRequest cheapestQuotesRequest =
        aCheapestQuotesRequest().withDefaultValues().build();

    final String errorJson =
        "{\"errorDescription\": \"Skyscanner quote response included validation errors.\", \"errorType\": \"BAD_REQUEST\", \"providerErrors\": null}";

    given(mockRestTemplate.exchange(CHEAPEST_QUOTES_URL, GET,
        new HttpEntity<CheapestQuotesRequest>(cheapestQuotesRequest),
        SkyscannerCheapestQuotesResponse.class))
            .willThrow(buildHttpStatusCodeException(BAD_REQUEST, errorJson));

    expectedException.expect(SkyscannerAdaptorBadRequestException.class);
    expectedException.expectMessage(VALIDATION_MESSAGE);

    client.getCheapestQuotes(cheapestQuotesRequest);


  }

  @Test
  public void shouldThrowSkyscannerServerErrorForUnexpectedValidationError() {

    final CheapestQuotesRequest cheapestQuotesRequest =
        aCheapestQuotesRequest().withDefaultValues().build();

    final String errorJson = "{\"error\":\"Some unexpected json response\"}";

    given(mockRestTemplate.exchange(CHEAPEST_QUOTES_URL, GET,
        new HttpEntity<CheapestQuotesRequest>(cheapestQuotesRequest),
        SkyscannerCheapestQuotesResponse.class))
            .willThrow(buildHttpStatusCodeException(BAD_REQUEST, errorJson));

    expectedException.expect(SkyscannerAdaptorServerException.class);
    expectedException.expectMessage(
        ERROR_MESSAGE + " Error: Could not unmarshal error response. Response was: " + errorJson);

    client.getCheapestQuotes(cheapestQuotesRequest);


  }

  @Test
  public void shouldThrowSkyscannerServerErrorFor500() {

    final CheapestQuotesRequest cheapestQuotesRequest =
        aCheapestQuotesRequest().withDefaultValues().build();

    final String errorJson = "Something went terribly wrong";

    given(mockRestTemplate.exchange(CHEAPEST_QUOTES_URL, GET,
        new HttpEntity<CheapestQuotesRequest>(cheapestQuotesRequest),
        SkyscannerCheapestQuotesResponse.class))
            .willThrow(buildHttpStatusCodeException(INTERNAL_SERVER_ERROR, errorJson));

    expectedException.expect(SkyscannerAdaptorServerException.class);
    expectedException.expectMessage(ERROR_MESSAGE + " Error: Internal Server Error.");

    client.getCheapestQuotes(cheapestQuotesRequest);


  }

  private HttpClientErrorException buildHttpStatusCodeException(final HttpStatus statusCode,
      final String responseBody) {
    return new HttpClientErrorException(statusCode, statusCode.getReasonPhrase(),
        responseBody.getBytes(), forName("UTF-8"));
  }

}
