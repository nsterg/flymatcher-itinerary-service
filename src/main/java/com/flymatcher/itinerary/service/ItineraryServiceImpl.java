package com.flymatcher.itinerary.service;

import static java.lang.Double.compare;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.itinerary.skyscannerclient.SkyscannerAdaptorClient;
import com.flymatcher.itinerary.transformer.ItineraryRequestTransformer;
import com.flymatcher.skyscanner.adaptor.api.CheapestQuotesRequest;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerQuote;

public class ItineraryServiceImpl implements ItineraryService {

  private final SkyscannerAdaptorClient skyscannerAdaptorClient;
  private final ItineraryRequestTransformer transformer;
  private final List<String> countries;

  public ItineraryServiceImpl(final SkyscannerAdaptorClient skyscannerAdaptorClient,
      final ItineraryRequestTransformer transformer, final List<String> countries) {
    this.skyscannerAdaptorClient = skyscannerAdaptorClient;
    this.transformer = transformer;
    this.countries = countries;
  }

  @Override
  public List<FlightMatch> findFlightMatches(final ItineraryRequest itineraryRequest) {

    final List<SkyscannerCheapestQuotesResponse> cheapestQuotesResponses = new ArrayList<>();

    countries.forEach(c -> {
      final List<CheapestQuotesRequest> requests = transformer.transform(itineraryRequest, c);
      requests.forEach(r -> {
        cheapestQuotesResponses.add(skyscannerAdaptorClient.getCheapestQuotes(r));

      });
    });

    return processFlightMatches(cheapestQuotesResponses);

  }

  private List<FlightMatch> processFlightMatches(
      final List<SkyscannerCheapestQuotesResponse> cheapestQuotesResponses) {
    final List<SkyscannerQuote> skyscannerQuote = new ArrayList<>();

    cheapestQuotesResponses.forEach(r -> {
      skyscannerQuote.addAll(r.getQuotes());
    });

    final Set<String> matchingDestinations = new HashSet<>();

    final Map<String, FlightMatch> flightMatchMap = new HashMap<>();
    skyscannerQuote.forEach(q -> {
      final String destination = q.getOutboundLeg().getDestination();
      final FlightMatch flightMatch = flightMatchMap.get(destination);
      if (flightMatch == null) {
        final FlightMatch fm = new FlightMatch();
        fm.setDestination(destination);
        fm.setInboundDate(q.getInboundLeg().getDepartureDate().toLocalDate());
        fm.setOutboundDate(q.getOutboundLeg().getDepartureDate().toLocalDate());
        fm.setPrice(q.getPrice());
        flightMatchMap.put(destination, fm);
      } else {
        flightMatch.setPrice(flightMatch.getPrice() + q.getPrice());
        matchingDestinations.add(destination);
      }
    });

    return flightMatchMap.values().stream()
        .filter(n -> !matchingDestinations.add(n.getDestination()))
        .sorted((f1, f2) -> compare(f1.getPrice(), f2.getPrice())).collect(toList());

  }

}
