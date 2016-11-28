package com.flymatcher.itinerary.domain.builders;

import static java.time.LocalDate.parse;
import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.util.List;

import com.flymatcher.itinerary.domain.ItineraryRequest;


public class ItineraryRequestBuilder {

  private String market;
  private List<String> origins;
  private String currency;
  private String locale;
  private LocalDate outboundPartialDate;
  private LocalDate inboundPartialDate;

  private ItineraryRequestBuilder() {}

  public static ItineraryRequestBuilder anItineraryRequest() {
    return new ItineraryRequestBuilder();
  }

  public ItineraryRequest build() {
    final ItineraryRequest itineraryRequest = new ItineraryRequest();
    itineraryRequest.setOrigins(origins);
    itineraryRequest.setMarket(market);
    itineraryRequest.setCurrency(currency);
    itineraryRequest.setLocale(locale);
    itineraryRequest.setOutboundPartialDate(outboundPartialDate);
    itineraryRequest.setInboundPartialDate(inboundPartialDate);

    return itineraryRequest;
  }

  public ItineraryRequestBuilder withMarket(final String market) {
    this.market = market;
    return this;
  }

  public ItineraryRequestBuilder withOrigins(final List<String> origins) {
    this.origins = origins;
    return this;
  }

  public ItineraryRequestBuilder withCurrency(final String currency) {
    this.currency = currency;
    return this;
  }

  public ItineraryRequestBuilder withLocale(final String locale) {
    this.locale = locale;
    return this;
  }

  public ItineraryRequestBuilder withOutboundPartialDate(final LocalDate outboundPartialDate) {
    this.outboundPartialDate = outboundPartialDate;
    return this;
  }

  public ItineraryRequestBuilder withOutboundPartialDate(final String outboundPartialDate) {
    this.outboundPartialDate = parse(outboundPartialDate);
    return this;
  }

  public ItineraryRequestBuilder withInboundPartialDate(final LocalDate inboundPartialDate) {
    this.inboundPartialDate = inboundPartialDate;
    return this;
  }

  public ItineraryRequestBuilder withInboundPartialDate(final String inboundPartialDate) {
    this.inboundPartialDate = parse(inboundPartialDate);
    return this;
  }

  public ItineraryRequestBuilder withDefaultValues() {
    this.market = "GR";
    this.origins = asList("ATH", "MAD");
    this.currency = "EUR";
    this.locale = "en-GB";
    this.outboundPartialDate = parse("2016-10-10");
    this.inboundPartialDate = parse("2016-10-20");

    return this;
  }

}
