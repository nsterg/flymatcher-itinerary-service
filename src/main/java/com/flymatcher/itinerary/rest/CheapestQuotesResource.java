package com.flymatcher.itinerary.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerQuote;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class CheapestQuotesResource {

  @RequestMapping(value = "/v1/cheapest-quotes", method = GET)
  // @formatter:off
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Success", response = SkyscannerCheapestQuotesResponse.class)})
  // @formatter:on
  public ResponseEntity<? extends Object> getCheapestQuotes(
      @RequestParam("country") final String country, @RequestParam("city") final String city,
      @RequestParam("currency") final String curerncy,
      @RequestParam("departureDate") final String departureDate) {

    // TODO - invoke service call to handle the request
    final SkyscannerCheapestQuotesResponse response = new SkyscannerCheapestQuotesResponse();
    response.setQuotes(new ArrayList<SkyscannerQuote>());

    return new ResponseEntity<SkyscannerCheapestQuotesResponse>(response, OK);

  }

}
