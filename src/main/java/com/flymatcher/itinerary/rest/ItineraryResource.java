package com.flymatcher.itinerary.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flymatcher.itinerary.FlightMatch;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class ItineraryResource {

  @RequestMapping(value = "/v1/itineraries", method = GET)
  // @formatter:off
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Success", response = FlightMatch.class)})
  // @formatter:on
  public ResponseEntity<? extends Object> findPriceMatch(
      @RequestParam("origins") final List<String> origins,
      @RequestParam("outboundDate") final String outboundDate,
      @RequestParam("inboundDate") final String inboundDate) {
    // TODO - invoke service call to handle the request


    final FlightMatch match2 = new FlightMatch();

    match2.setDestination("MILAN");
    match2.setPrice(150);
    match2.setInboundDate(LocalDate.parse(inboundDate));
    match2.setOutboundDate(LocalDate.parse(outboundDate));

    final FlightMatch match1 = new FlightMatch();

    match1.setDestination("PARIS");
    match1.setPrice(100);
    match1.setInboundDate(LocalDate.parse(inboundDate));
    match1.setOutboundDate(LocalDate.parse(outboundDate));



    return new ResponseEntity<List<FlightMatch>>(Arrays.asList(match1, match2), OK);

  }

}
