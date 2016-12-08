package com.flymatcher.itinerary.skyscanneradaptorclient;

import com.flymatcher.skyscanner.adaptor.api.CheapestQuotesRequest;
import com.flymatcher.skyscanner.adaptor.api.SkyscannerCheapestQuotesResponse;

public interface SkyscannerAdaptorClient {

  SkyscannerCheapestQuotesResponse getCheapestQuotes(CheapestQuotesRequest request);

}
