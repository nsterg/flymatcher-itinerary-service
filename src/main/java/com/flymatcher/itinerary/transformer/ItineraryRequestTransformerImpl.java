package com.flymatcher.itinerary.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.domain.ItineraryRequest;

@Component
public class ItineraryRequestTransformerImpl implements ItineraryRequestTransformer {

  @Override
  public List<CheapestQuotesRequest> transform(final ItineraryRequest itineraryRequest,
      final String destinationCountry) {

    final List<CheapestQuotesRequest> retList = new ArrayList<>();
    itineraryRequest.getOrigins().forEach(o -> {
      final CheapestQuotesRequest cheapestQuotesRequest = new CheapestQuotesRequest();
      cheapestQuotesRequest.setCurrency(itineraryRequest.getCurrency());
      cheapestQuotesRequest.setLocale(itineraryRequest.getLocale());
      cheapestQuotesRequest.setMarket(itineraryRequest.getMarket());
      cheapestQuotesRequest.setInboundPartialDate(itineraryRequest.getInboundPartialDate());
      cheapestQuotesRequest.setOutboundPartialDate(itineraryRequest.getOutboundPartialDate());
      cheapestQuotesRequest.setDestinationCountry(destinationCountry);
      cheapestQuotesRequest.setOriginCity(o);
      retList.add(cheapestQuotesRequest);
    });



    return retList;
  }

}
