package com.flymatcher.itinerary.skyscanneradaptorclient;

import com.flymatcher.itinerary.domain.CheapestQuotesRequest;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;

public interface SkyscannerAdaptorClient {

  SkyscannerCheapestQuotesResponse getCheapestQuotes(CheapestQuotesRequest request);

}
