package com.flymatcher.itinerary.rest;

import static com.flymatcher.itinerary.domain.ItineraryRequest.valueOf;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.service.ItineraryService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class ItineraryResource {

  private final ItineraryService itineraryService;

  @Autowired
  public ItineraryResource(final ItineraryService itineraryService) {
    this.itineraryService = itineraryService;
  }

  @RequestMapping(value = "/v1/itineraries", method = GET)
  // @formatter:off
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Success", response = FlightMatch.class)})
  // @formatter:on
  public ResponseEntity<? extends Object> findFlightMatch(
      @RequestParam("origins") final List<String> origins,
      @RequestParam("outboundDate") final String outboundDate,
      @RequestParam("inboundDate") final String inboundDate) {

    // TODO get market, locale from UI
    final List<FlightMatch> flightMatches = itineraryService
        .findFlightMatches(valueOf("GR", origins, "EUR", "en-GB", outboundDate, inboundDate));

    return new ResponseEntity<List<FlightMatch>>(flightMatches, OK);

  }

}
