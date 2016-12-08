package com.flymatcher.itinerary.transformer;

import static com.flymatcher.itinerary.domain.builders.ItineraryRequestBuilder.anItineraryRequest;
import static com.flymatcher.skyscanner.adaptor.api.builders.CheapestQuotesRequestBuilder.aCheapestQuotesRequest;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.skyscanner.adaptor.api.CheapestQuotesRequest;

public class ItineraryRequestTransformerTest {

  @Test
  public void shouldTransform() {

    final String destinationCountry = "GR";

    final ItineraryRequestTransformer transformer = new ItineraryRequestTransformerImpl();

    final ItineraryRequest itineraryRequest = anItineraryRequest().withDefaultValues().build();

    final CheapestQuotesRequest request1 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(destinationCountry).withOriginCity("ATH").build();
    final CheapestQuotesRequest request2 = aCheapestQuotesRequest().withDefaultValues()
        .withDestinationCountry(destinationCountry).withOriginCity("MAD").build();

    final List<CheapestQuotesRequest> actual =
        transformer.transform(itineraryRequest, destinationCountry);

    assertEquals("Did not match expected CheapestQuotesRequest list", asList(request1, request2),
        actual);

  }

}
