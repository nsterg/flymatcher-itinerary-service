package com.flymatcher.itinerary.skyscanneradaptorclient;

import static javax.ws.rs.core.UriBuilder.fromPath;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flymatcher.error.FlymatcherError;
import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.exception.SkyscannerAdaptorBadRequestException;
import com.flymatcher.itinerary.exception.SkyscannerAdaptorServerException;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;

@Component
public class SkyscannerAdaptorClientImpl implements SkyscannerAdaptorClient {


  private final RestTemplate adaptorRestTemplate;
  private final String adaptorCheapestQuotesUrl;
  private final ObjectMapper objectMapper;

  private static final String ERROR_MESSAGE = "Could not get a valid skyscanner adaptor response.";
  private static final String VALIDATION_MESSAGE =
      "Skyscanner adaptor response included validation errors.";

  private static final String SKYSCANNER_ADAPTOR_PATH_URL =
      "{market}/{currency}/{locale}/{city}/{destinationCountry}/{outboundPartialDate}/{inboundPartialDate}";

  @Autowired
  public SkyscannerAdaptorClientImpl(final RestTemplate restTemplate,
      @Value("${skyscanner-adaptor.cheapest-quotes-base-url}") final String adaptorCheapestQuotesBaseUrl,
      final ObjectMapper objectMapper) {
    this.adaptorCheapestQuotesUrl =
        adaptorCheapestQuotesBaseUrl + "/v1/cheapest-quotes/" + SKYSCANNER_ADAPTOR_PATH_URL;

    this.adaptorRestTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public SkyscannerCheapestQuotesResponse getCheapestQuotes(final CheapestQuotesRequest request) {

    ResponseEntity<SkyscannerCheapestQuotesResponse> responseEntity = null;
    try {
      responseEntity = adaptorRestTemplate.exchange(buildUrl(request), GET, null,
          SkyscannerCheapestQuotesResponse.class);
      return responseEntity.getBody();

    } catch (final HttpStatusCodeException e) {
      if (e.getStatusCode() == BAD_REQUEST) {
        FlymatcherError error = null;
        try {
          error = objectMapper.readValue(e.getResponseBodyAsString(), FlymatcherError.class);
        } catch (final Throwable throwable) {
          throw new SkyscannerAdaptorServerException(
              ERROR_MESSAGE + " Error: Could not unmarshal error response. Response was: "
                  + e.getResponseBodyAsString());

        }
        throw new SkyscannerAdaptorBadRequestException(VALIDATION_MESSAGE, error);
      }

      throw new SkyscannerAdaptorServerException(ERROR_MESSAGE + " Error: Internal Server Error.");
    }
  }

  private String buildUrl(final CheapestQuotesRequest request) {
    // @formatter:off
    return fromPath(adaptorCheapestQuotesUrl).build(request.getMarket(), 
                                             request.getCurrency(),
                                             request.getLocale(),
                                             request.getOriginCity(),
                                             request.getDestinationCountry(),
                                             request.getOutboundPartialDate().toString(),
                                             request.getInboundPartialDate().toString())
        .toString();
    // @formatter:on
  }

}
