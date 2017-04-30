package com.flymatcher.itinerary.rest;

import static com.flymatcher.itinerary.domain.ItineraryRequest.valueOf;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.service.CheapestFlightsAggregator;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class ItineraryResource {

  private final CheapestFlightsAggregator itineraryService;

  @Autowired
  public ItineraryResource(final CheapestFlightsAggregator itineraryService) {
    this.itineraryService = itineraryService;
  }

  @RequestMapping(
      value = "/v1/itineraries/{market}/{currency}/{locale}/{origins}/{outboundDate}/{inboundDate}",
      method = GET)
  // @formatter:off
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Success", response = FlightMatch.class)})
  // @formatter:on
  public ResponseEntity<? extends Object> findFlightMatch(@PathVariable final String market,
      @PathVariable final String currency, @PathVariable final String locale,
      @PathVariable final List<String> origins, @PathVariable final String outboundDate,
      @PathVariable final String inboundDate) {


    final List<FlightMatch> flightMatches = itineraryService
        .findFlightMatches(valueOf(market, currency, locale, origins, outboundDate, inboundDate));

    return new ResponseEntity<List<FlightMatch>>(flightMatches, OK);

  }

}
