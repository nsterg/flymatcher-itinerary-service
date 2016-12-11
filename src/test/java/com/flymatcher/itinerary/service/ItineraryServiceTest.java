package com.flymatcher.itinerary.service;

import static com.flymatcher.itinerary.builders.FlightMatchBuilder.aFlightMatchBuilder;
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
import com.flymatcher.itinerary.builders.FlightMatchBuilder;
import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.itinerary.skyscanneradaptorclient.SkyscannerAdaptorClient;
import com.flymatcher.itinerary.transformer.ItineraryRequestTransformer;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;
import com.flymatcher.skyscanner.adaptor.api.builders.LegBuilder;

public class ItineraryServiceTest {

  @Mock
  private SkyscannerAdaptorClient mockClient;
  @Mock
  private ItineraryRequestTransformer mockTransformer;

  private static final String DESTINATION_COUNTRY = "FR";
  private static final List<String> COUNTRIES = asList(DESTINATION_COUNTRY);

  private static final String ORIGIN1 = "ATH";
  private static final String ORIGIN2 = "MAD";

  private static final String DESTINATION = "PARIS";
  private static final String CARRIER = "EasyJet";

  private static final String INBOUND_DATE_TIME = "2016-10-20T00:00:00";
  private static final String OUTBOUND_DATE_TIME = "2016-10-10T00:00:00";
  private static final String INBOUND_DATE = "2016-10-20";
  private static final String OUTBOUND_DATE = "2016-10-10";

  private ItineraryService service;

  @Before
  public void setUp() {
    initMocks(this);
    service = new ItineraryServiceImpl(mockClient, mockTransformer, COUNTRIES);
  }

  @Test
  public void shouldFindSingleFlightMatchForSingleDestination() throws ParseException {

    final FlightMatch expected = expectedFlightMatch(DESTINATION).withPrice(100.0).build();

    final ItineraryRequest itineraryRequest = anItineraryRequest().withDefaultValues().build();


    final CheapestQuotesRequest request1 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN1).build();
    final CheapestQuotesRequest request2 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN2).build();

    given(mockTransformer.transform(itineraryRequest, DESTINATION_COUNTRY))
        .willReturn(asList(request1, request2));

    // @formatter:off
    final SkyscannerCheapestQuotesResponse cheapestQuotesResponse1 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                                            aSkyscannerQuote().withDirect(true).withPrice(62)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN1, DESTINATION))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN1, DESTINATION)))
                                                          .build();
    
    final SkyscannerCheapestQuotesResponse cheapestQuotesResponse2 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                                            aSkyscannerQuote().withDirect(true).withPrice(38)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN2, DESTINATION))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN2, DESTINATION)))
                                                          .build();
    // @formatter:on

    given(mockClient.getCheapestQuotes(request1)).willReturn(cheapestQuotesResponse1);
    given(mockClient.getCheapestQuotes(request2)).willReturn(cheapestQuotesResponse2);

    final List<FlightMatch> actual = service.findFlightMatches(itineraryRequest);

    assertEquals(actual.size(), 1);

    assertEquals(expected, actual.get(0));

  }

  @Test
  public void shouldFindAndOrderMultipleFlightMatchesForSingleCountry() throws ParseException {

    final FlightMatch expected1 = expectedFlightMatch("LDN").withPrice(100.0).build();
    final FlightMatch expected2 = expectedFlightMatch("PAR").withPrice(150.0).build();
    final FlightMatch expected3 = expectedFlightMatch("LIS").withPrice(200.0).build();


    final ItineraryRequest itineraryRequest = anItineraryRequest().withDefaultValues().build();


    final CheapestQuotesRequest request1 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN1).build();
    final CheapestQuotesRequest request2 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(DESTINATION_COUNTRY).withOriginCity(ORIGIN2).build();

    given(mockTransformer.transform(itineraryRequest, DESTINATION_COUNTRY))
        .willReturn(asList(request1, request2));

    // @formatter:off
    final SkyscannerCheapestQuotesResponse cheapestQuotesResponse1 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                                            aSkyscannerQuote().withDirect(true).withPrice(82)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN1, "PAR"))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "PAR")),
                                                              aSkyscannerQuote().withDirect(true).withPrice(62)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN1, "LDN"))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "LDN")),
                                                              aSkyscannerQuote().withDirect(true).withPrice(102)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN1, "LIS"))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "LIS")),
                                                              aSkyscannerQuote().withDirect(true).withPrice(62)
                                                              .withInboundLeg(buildInBoundLeg(ORIGIN1, "SOF"))
                                                              .withOutboundLeg(buildOutBoundLeg(ORIGIN1, "SOF")))
                                                          .build();
    
    final SkyscannerCheapestQuotesResponse cheapestQuotesResponse2 = aSkyscannerCheapestQuotesResponse().withQuotes(
                                                          aSkyscannerQuote().withDirect(true).withPrice(68)
                                                            .withInboundLeg(buildInBoundLeg(ORIGIN2, "PAR"))
                                                            .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "PAR")),
                                                            aSkyscannerQuote().withDirect(true).withPrice(38)
                                                            .withInboundLeg(buildInBoundLeg(ORIGIN2, "LDN"))
                                                            .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "LDN")),
                                                            aSkyscannerQuote().withDirect(true).withPrice(98)
                                                            .withInboundLeg(buildInBoundLeg(ORIGIN2, "LIS"))
                                                            .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "LIS")),
                                                            aSkyscannerQuote().withDirect(true).withPrice(62)
                                                            .withInboundLeg(buildInBoundLeg(ORIGIN2, "BUC"))
                                                            .withOutboundLeg(buildOutBoundLeg(ORIGIN2, "BUC")))
                                                        .build();
    // @formatter:on

    given(mockClient.getCheapestQuotes(request1)).willReturn(cheapestQuotesResponse1);
    given(mockClient.getCheapestQuotes(request2)).willReturn(cheapestQuotesResponse2);

    final List<FlightMatch> actual = service.findFlightMatches(itineraryRequest);

    assertEquals(asList(expected1, expected2, expected3), actual);

  }

  private FlightMatchBuilder expectedFlightMatch(final String destination) {
    return aFlightMatchBuilder().withDestination(destination).withInboundDate(INBOUND_DATE)
        .withOutboundDate(OUTBOUND_DATE);
  }


  private LegBuilder buildOutBoundLeg(final String origin, final String destination)
      throws ParseException {
    return aLeg().withCarrier(CARRIER).withOrigin(origin).withDestination(destination)
        .withDepartureDate(OUTBOUND_DATE_TIME);
  }

  private LegBuilder buildInBoundLeg(final String origin, final String destination)
      throws ParseException {
    return aLeg().withCarrier(CARRIER).withOrigin(origin).withDestination(destination)
        .withDepartureDate(INBOUND_DATE_TIME);
  }

}
