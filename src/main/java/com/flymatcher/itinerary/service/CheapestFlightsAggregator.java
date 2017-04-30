package com.flymatcher.itinerary.service;

import java.util.List;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.domain.ItineraryRequest;

public interface CheapestFlightsAggregator {

  List<FlightMatch> findFlightMatches(final ItineraryRequest itineraryRequest);

}
