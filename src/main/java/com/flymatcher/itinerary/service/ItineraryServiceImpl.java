package com.flymatcher.itinerary.service;

import static java.lang.Double.compare;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.Route;
import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.itinerary.skyscanneradaptorclient.SkyscannerAdaptorClient;
import com.flymatcher.itinerary.transformer.ItineraryRequestTransformer;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerQuote;

@Component
public class ItineraryServiceImpl implements ItineraryService {

  private final SkyscannerAdaptorClient skyscannerAdaptorClient;
  private final ItineraryRequestTransformer transformer;
  private final List<String> countries;

  @Autowired
  public ItineraryServiceImpl(final SkyscannerAdaptorClient skyscannerAdaptorClient,
      final ItineraryRequestTransformer transformer,
      @Value("#{'${skyscanner.european.airports}'.split(',')}") final List<String> countries) {
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
        flightMatchMap.put(destination, createFlightMatch(q, destination));
      } else {
        flightMatch.setPrice(flightMatch.getPrice() + q.getPrice());
        flightMatch.getRoutes().add(createRoute(q));
        matchingDestinations.add(destination);
      }

    });

    return flightMatchMap.values().stream()
        .filter(n -> !matchingDestinations.add(n.getDestination()))
        .sorted((f1, f2) -> compare(f1.getPrice(), f2.getPrice())).collect(toList());

  }

  private FlightMatch createFlightMatch(final SkyscannerQuote q, final String destination) {
    final FlightMatch fm = new FlightMatch();

    fm.setDestination(destination);
    fm.setInboundDate(q.getInboundLeg().getDepartureDate().toLocalDate());
    fm.setOutboundDate(q.getOutboundLeg().getDepartureDate().toLocalDate());
    fm.setPrice(q.getPrice());
    final List<Route> routes = new ArrayList<>();
    routes.add(createRoute(q));
    fm.setRoutes(routes);
    // fm.setAirportCode(q.getOutboundLeg().getAirportCode());
    fm.setCountry(q.getOutboundLeg().getCountry());

    return fm;
  }

  private Route createRoute(final SkyscannerQuote q) {
    final Route route = new Route();
    route.setDestinationAirport(q.getOutboundLeg().getDestination());
    route.setDestinationAirportCode(q.getOutboundLeg().getDestinationCode());
    route.setOriginAirport(q.getOutboundLeg().getOrigin());
    route.setOriginAirportCode(q.getOutboundLeg().getOriginCode());
    route.setPrice(q.getPrice());
    return route;
  }

}
