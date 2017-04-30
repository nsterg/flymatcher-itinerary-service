package com.flymatcher.itinerary.service;

import static com.flymatcher.itinerary.builders.FlightMatchBuilder.aFlightMatchBuilder;
import static com.flymatcher.itinerary.builders.RouteBuilder.aRoute;
import static com.flymatcher.itinerary.domain.builders.CheapestQuotesRequestBuilder.aCheapestQuotesRequest;
import static com.flymatcher.itinerary.domain.builders.ItineraryRequestBuilder.anItineraryRequest;
import static com.flymatcher.skyscanner.adaptor.api.builders.LegBuilder.aLeg;
import static com.flymatcher.skyscanner.adaptor.api.builders.SkyscannerCheapestQuotesResponseBuilder.aSkyscannerCheapestQuotesResponse;
import static com.flymatcher.skyscanner.adaptor.api.builders.SkyscannerQuoteBuilder.aSkyscannerQuote;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

import java.text.ParseException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.flymatcher.itinerary.FlightMatch;
import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.itinerary.skyscanneradaptorclient.SkyscannerAdaptorClient;
import com.flymatcher.itinerary.transformer.ItineraryRequestTransformer;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;
import com.flymatcher.skyscanner.adaptor.api.builders.LegBuilder;

public class CheapestFlightsAggregatorTest {

  @Mock
  private SkyscannerAdaptorClient mockClient;
  @Mock
  private ItineraryRequestTransformer mockTransformer;

  private static final String DESTINATION_COUNTRY = "DE";
  private static final String ANYWHERE = "anywhere";

  private static final String ORIGIN1 = "ATH";
  private static final String ORIGIN2 = "MAD";

  private static final String CARRIER = "EasyJet";

  private static final String INBOUND_DATE_TIME = "2016-10-20T00:00:00";
  private static final String OUTBOUND_DATE_TIME = "2016-10-10T00:00:00";
  private static final String INBOUND_DATE = "2016-10-20";
  private static final String OUTBOUND_DATE = "2016-10-10";

  private CheapestFlightsAggregator aggregator;

  @Before
  public void setUp() {
    initMocks(this);
    aggregator = new CheapestFlightAggregatorImpl(mockClient, mockTransformer, 1);
  }


  @Test
  public void shouldFindAndOrderMultipleFlightMatchesForSingleCountry() throws ParseException {

    // @formatter:off
    
    
    
    final FlightMatch expected1 = aFlightMatchBuilder()
                                    .withDestination("London Gatwick")
                                    .withInboundDate(INBOUND_DATE)
                                    .withOutboundDate(OUTBOUND_DATE)
                                    .withPrice(100.0)
                                    .withCountry("United Kingdom")
                                    .withRoutes(aRoute().withDestinationAirport("London Gatwick")
                                                        .withDestinationAirportCode("LDN")
                                                        .withOriginAirport("Athens")
                                                        .withOriginAirportCode("ATH")
                                                        .withPrice(62.0),
                                                  aRoute().withDestinationAirport("London Gatwick")
                                                        .withDestinationAirportCode("LDN")
                                                        .withOriginAirport("Madrid")
                                                        .withOriginAirportCode("MAD")
                                                        .withPrice(38.0)
                                                )
                                  .build();

    final FlightMatch expected2 = aFlightMatchBuilder()
                                    .withDestination("Paris")
                                    .withInboundDate(INBOUND_DATE)
                                    .withOutboundDate(OUTBOUND_DATE)
                                    .withPrice(150.0)
                                    .withCountry("France")
                                    .withRoutes(aRoute().withDestinationAirport("Paris")
                                                        .withDestinationAirportCode("PAR")
                                                        .withOriginAirport("Athens")
                                                        .withOriginAirportCode("ATH")
                                                        .withPrice(82.0),
                                                aRoute().withDestinationAirport("Paris")
                                                        .withDestinationAirportCode("PAR")
                                                        .withOriginAirport("Madrid")
                                                        .withOriginAirportCode("MAD")
                                                        .withPrice(68.0)
                                              )                                    
                                  .build();

    final FlightMatch expected3 = aFlightMatchBuilder()
                                    .withDestination("Lisbon")
                                    .withInboundDate(INBOUND_DATE)
                                    .withOutboundDate(OUTBOUND_DATE)
                                    .withPrice(200.0)
                                    .withCountry("Portugal")
                                    .withRoutes(aRoute().withDestinationAirport("Lisbon")
                                                        .withDestinationAirportCode("LIS")
                                                        .withOriginAirport("Athens")
                                                        .withOriginAirportCode("ATH")
                                                        .withPrice(102.0),
                                                aRoute().withDestinationAirport("Lisbon")
                                                        .withDestinationAirportCode("LIS")
                                                        .withOriginAirport("Madrid")
                                                        .withOriginAirportCode("MAD")
                                                        .withPrice(98.0)
                                              )                                    
                                  .build();
    
    final FlightMatch expected4 = aFlightMatchBuilder()
                                    .withDestination("Frankfurt")
                                    .withInboundDate(INBOUND_DATE)
                                    .withOutboundDate(OUTBOUND_DATE)
                                    .withPrice(300.0)
                                    .withCountry("Germany")
                                    .withRoutes(aRoute().withDestinationAirport("Frankfurt")
                                                        .withDestinationAirportCode("FRN")
                                                        .withOriginAirport("Athens")
                                                        .withOriginAirportCode("ATH")
                                                        .withPrice(152.0),
                                                aRoute().withDestinationAirport("Frankfurt")
                                                        .withDestinationAirportCode("FRN")
                                                        .withOriginAirport("Madrid")
                                                        .withOriginAirportCode("MAD")
                                                        .withPrice(148.0)
                                              )                                    
                                  .build();
    
    final SkyscannerCheapestQuotesResponse cheapestQuotesAnywhereResponse1 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                  aSkyscannerQuote().withDirect(true).withPrice(82)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN1, "PAR", "Athens", "Paris", "FR", "France"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "PAR", "Athens", "Paris", "FR", "France")),
                                    aSkyscannerQuote().withDirect(true).withPrice(62)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN1, "LDN", "Athens", "London Gatwick", "UK", "United Kingdom"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "LDN", "Athens", "London Gatwick", "UK", "United Kingdom")),
                                    aSkyscannerQuote().withDirect(true).withPrice(102)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN1, "LIS", "Athens", "Lisbon", "PT", "Portugal"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "LIS", "Athens", "Lisbon", "PT", "Portugal")),
                                    aSkyscannerQuote().withDirect(true).withPrice(62)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN1, "BRL", "Athens", "Berlin", "DE", "Germany"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "BRL", "Athens", "Berlin", "DE", "Germany")))
                                .build();
    
    final SkyscannerCheapestQuotesResponse cheapestQuotesAnywhereResponse2 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                  aSkyscannerQuote().withDirect(true).withPrice(68)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN2, "PAR", "Madrid", "Paris", "FR", "France"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "PAR", "Madrid", "Paris", "FR", "France")),
                                    aSkyscannerQuote().withDirect(true).withPrice(38)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN2, "LDN", "Madrid", "London Gatwick", "UK", "United Kingdom"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "LDN", "Madrid", "London Gatwick", "UK", "United Kingdom")),
                                    aSkyscannerQuote().withDirect(true).withPrice(98)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN2, "LIS",  "Madrid", "Lisbon", "PT", "Portugal"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "LIS",  "Madrid", "Lisbon", "PT", "Portugal")),
                                    aSkyscannerQuote().withDirect(true).withPrice(62)
                                    .withInboundLeg(buildInBoundLeg(ORIGIN2, "FR", "Madrid", "Frankurt", "DE", "Germany"))
                                    .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "FR","Madrid", "Frankurt", "DE", "Germany")))
                                .build();
    
    final SkyscannerCheapestQuotesResponse cheapestQuotesCountryResponse1 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                aSkyscannerQuote().withDirect(true).withPrice(152)
                                  .withInboundLeg(buildInBoundLeg(ORIGIN1, "FRN", "Athens", "Frankfurt", "DE", "Germany"))
                                  .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "FRN", "Athens", "Frankfurt", "DE", "Germany")),
                                  aSkyscannerQuote().withDirect(true).withPrice(1000)
                                  .withInboundLeg(buildInBoundLeg(ORIGIN1, "HMB", "Athens", "Hamburg", "DE", "Germany"))
                                  .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "HMB", "Athens", "Hamburg", "DE", "Germany")))
                              .build();

final SkyscannerCheapestQuotesResponse cheapestQuotesCountryResponse2 = aSkyscannerCheapestQuotesResponse().withQuotes(
                              aSkyscannerQuote().withDirect(true).withPrice(148)
                                .withInboundLeg(buildInBoundLeg(ORIGIN2, "FRN", "Madrid", "Frankfurt", "DE", "Germany"))
                                .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "FRN", "Madrid", "Frankfurt", "DE", "Germany")),
                                aSkyscannerQuote().withDirect(true).withPrice(500)
                                .withInboundLeg(buildInBoundLeg(ORIGIN2, "BHM", "Madrid", "Bohum", "DE", "Germany"))
                                .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "BHM", "Madrid", "Bohum", "DE", "Germany")))
                            .build();
    // @formatter:on



    final ItineraryRequest itineraryRequest = anItineraryRequest().withDefaultValues().build();


    final CheapestQuotesRequest anywhereRequest1 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(ANYWHERE).withOriginCity(ORIGIN1).build();
    final CheapestQuotesRequest anywhereRequest2 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(ANYWHERE).withOriginCity(ORIGIN2).build();

    final CheapestQuotesRequest countryRequest1 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN1).build();
    final CheapestQuotesRequest countryRequest2 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN2).build();

    given(mockTransformer.transform(itineraryRequest, ANYWHERE))
        .willReturn(asList(anywhereRequest1, anywhereRequest2));

    given(mockTransformer.transform(itineraryRequest, DESTINATION_COUNTRY))
        .willReturn(asList(countryRequest1, countryRequest2));


    given(mockClient.getCheapestQuotes(anywhereRequest1))
        .willReturn(cheapestQuotesAnywhereResponse1);
    given(mockClient.getCheapestQuotes(anywhereRequest2))
        .willReturn(cheapestQuotesAnywhereResponse2);

    given(mockClient.getCheapestQuotes(countryRequest1)).willReturn(cheapestQuotesCountryResponse1);
    given(mockClient.getCheapestQuotes(countryRequest2)).willReturn(cheapestQuotesCountryResponse2);

    final List<FlightMatch> actual = aggregator.findFlightMatches(itineraryRequest);

    assertEquals(asList(expected1, expected2, expected3, expected4), actual);

  }


  private LegBuilder buildOutBoundLeg(final String originCode, final String destinationCode,
      final String origin, final String destination, final String countryCode, final String country)
      throws ParseException {
    return aLeg().withCarrier(CARRIER).withOrigin(origin).withDestinationCode(destinationCode)
        .withOriginCode(originCode).withDestination(destination).withCountry(country)
        .withCountryCode(countryCode).withDepartureDate(OUTBOUND_DATE_TIME);
  }

  private LegBuilder buildInBoundLeg(final String originCode, final String destinationCode,
      final String origin, final String destination, final String countryCode, final String country)
      throws ParseException {
    return aLeg().withCarrier(CARRIER).withOrigin(origin).withDestination(destination)
        .withOriginCode(originCode).withDestinationCode(destination).withCountry(country)
        .withCountryCode(countryCode).withDepartureDate(INBOUND_DATE_TIME);
  }

}
