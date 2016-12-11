package com.flymatcher.itinerary.transformer;

import java.util.List;

import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.itinerary.domain.ItineraryRequest;

public interface ItineraryRequestTransformer {

  List<CheapestQuotesRequest> transform(ItineraryRequest itineraryRequest,
      String destinationCOuntry);

}
