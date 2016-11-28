package com.flymatcher.itinerary.transformer;

import java.util.List;

import com.flymatcher.itinerary.domain.ItineraryRequest;
import com.flymatcher.skyscanner.adaptor.api.CheapestQuotesRequest;

public interface ItineraryRequestTransformer {

  List<CheapestQuotesRequest> transform(ItineraryRequest itineraryRequest,
      String destinationCOuntry);

}
