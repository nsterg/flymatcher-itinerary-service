package com.flymatcher.itinerary.service;

import static java.lang.Double.compare;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
public class CheapestFlightAggregatorImpl implements CheapestFlightsAggregator {

  private final SkyscannerAdaptorClient skyscannerAdaptorClient;
  private final ItineraryRequestTransformer transformer;
  private final Integer cheapestCountriesNum;

  @Autowired
  public CheapestFlightAggregatorImpl(final SkyscannerAdaptorClient skyscannerAdaptorClient,
      final ItineraryRequestTransformer transformer,
      @Value("${cheapest.countries.num}") final Integer cheapestCountriesNum) {
    this.skyscannerAdaptorClient = skyscannerAdaptorClient;
    this.transformer = transformer;
    this.cheapestCountriesNum = cheapestCountriesNum;
  }

  @Override
  public List<FlightMatch> findFlightMatches(final ItineraryRequest itineraryRequest) {


    final List<SkyscannerCheapestQuotesResponse> cheapestQuotesResponses =
        getCheapestQuotesResponses(itineraryRequest, singletonList("anywhere"));
    final List<FlightMatch> initialMatches = findFlightMatches(cheapestQuotesResponses);

    final List<String> cheapestCountries =
        findCheapestCountries(cheapestQuotesResponses, initialMatches);

    final List<FlightMatch> moreMatches =
        findFlightMatches(getCheapestQuotesResponses(itineraryRequest, cheapestCountries));

    return aggregateMatches(initialMatches, moreMatches);

  }


  private List<FlightMatch> aggregateMatches(final List<FlightMatch> initialMatches,
      final List<FlightMatch> moreMatches) {
    return concat(initialMatches.stream(), moreMatches.stream())
        .sorted((f1, f2) -> compare(f1.getPrice(), f2.getPrice())).collect(toList());
  }

  private List<String> findCheapestCountries(
      final List<SkyscannerCheapestQuotesResponse> cheapestQuotesResponses,
      final List<FlightMatch> initialMatches) {

    final List<SkyscannerQuote> skyscannerQuote = new ArrayList<>();

    cheapestQuotesResponses.forEach(r -> {
      skyscannerQuote.addAll(r.getQuotes());
    });


    final Map<String, Double> flightMatchMap = new HashMap<>();
    skyscannerQuote.forEach(q -> {

      final String country = q.getOutboundLeg().getCountry();
      final String countryCode = q.getOutboundLeg().getCountryCode();

      if (initialMatches.stream().filter(p -> p.getCountry().equals(country)).count() == 0) {
        final Double price = flightMatchMap.get(country);

        if (price == null) {
          flightMatchMap.put(countryCode, q.getPrice());
        } else {
          flightMatchMap.put(countryCode, q.getPrice() + price);

        }
      }

    });

    return flightMatchMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .limit(cheapestCountriesNum).map(Map.Entry::getKey).collect(Collectors.toList());
  }



  private List<SkyscannerCheapestQuotesResponse> getCheapestQuotesResponses(
      final ItineraryRequest itineraryRequest, final List<String> countries) {
    final List<SkyscannerCheapestQuotesResponse> cheapestQuotesResponses = new ArrayList<>();

    countries.forEach(c -> {
      final List<CheapestQuotesRequest> requests = transformer.transform(itineraryRequest, c);
      requests.forEach(r -> {
        cheapestQuotesResponses.add(skyscannerAdaptorClient.getCheapestQuotes(r));

      });
    });
    return cheapestQuotesResponses;
  }

  private List<FlightMatch> findFlightMatches(
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
        .filter(n -> !matchingDestinations.add(n.getDestination())).collect(toList());

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
