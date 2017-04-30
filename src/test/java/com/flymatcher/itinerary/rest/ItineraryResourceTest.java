package com.flymatcher.itinerary.rest;

import static com.flymatcher.itinerary.builders.FlightMatchBuilder.aFlightMatchBuilder;
import static com.flymatcher.itinerary.domain.builders.ItineraryRequestBuilder.anItineraryRequest;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.itinerary.service.CheapestFlightsAggregator;

public class ItineraryResourceTest {

  @Mock
  private CheapestFlightsAggregator mockItineraryService;

  private ItineraryResource resource;

  @Before
  public void setUp() {
    initMocks(this);
    resource = new ItineraryResource(mockItineraryService);

  }

  @Test
  public void shouldFindFlightMatches() {

    final List<FlightMatch> serviceResponse =
        asList(aFlightMatchBuilder().withDefaultValues().build());

    final ItineraryRequest itineraryRequest = anItineraryRequest().withDefaultValues().build();

    given(mockItineraryService.findFlightMatches(itineraryRequest)).willReturn(serviceResponse);

    final ResponseEntity<? extends Object> actual = resource.findFlightMatch("GR", "EUR", "en-GB",
        asList("ATH", "MAD"), "2016-10-10", "2016-10-20");

    assertEquals("Incorrect status", OK, actual.getStatusCode());
    assertEquals("Incorrect body", serviceResponse, actual.getBody());
  }
}
