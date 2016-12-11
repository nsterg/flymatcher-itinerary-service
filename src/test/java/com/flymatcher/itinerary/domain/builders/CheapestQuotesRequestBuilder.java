package com.flymatcher.itinerary.domain.builders;

import static java.time.LocalDate.parse;

import java.time.LocalDate;

import com.flymatcher.itinerary.domain.CheapestQuotesRequest;



public class CheapestQuotesRequestBuilder {

  private String market;
  private String originCity;
  private String destinationCountry;
  private String currency;
  private String locale;
  private LocalDate outboundPartialDate;
  private LocalDate inboundPartialDate;

  private CheapestQuotesRequestBuilder() {}

  public static CheapestQuotesRequestBuilder aCheapestQuotesRequest() {
    return new CheapestQuotesRequestBuilder();
  }

  public CheapestQuotesRequest build() {
    final CheapestQuotesRequest cheapestQuotesRequest = new CheapestQuotesRequest();
    cheapestQuotesRequest.setOriginCity(originCity);
    cheapestQuotesRequest.setDestinationCountry(destinationCountry);
    cheapestQuotesRequest.setMarket(market);
    cheapestQuotesRequest.setCurrency(currency);
    cheapestQuotesRequest.setLocale(locale);
    cheapestQuotesRequest.setOutboundPartialDate(outboundPartialDate);
    cheapestQuotesRequest.setInboundPartialDate(inboundPartialDate);

    return cheapestQuotesRequest;
  }

  public CheapestQuotesRequestBuilder withMarket(final String market) {
    this.market = market;
    return this;
  }

  public CheapestQuotesRequestBuilder withOriginCity(final String city) {
    this.originCity = city;
    return this;
  }

  public CheapestQuotesRequestBuilder withDestinationCountry(final String destinationCountry) {
    this.destinationCountry = destinationCountry;
    return this;
  }

  public CheapestQuotesRequestBuilder withCurrency(final String currency) {
    this.currency = currency;
    return this;
  }

  public CheapestQuotesRequestBuilder withLocale(final String locale) {
    this.locale = locale;
    return this;
  }

  public CheapestQuotesRequestBuilder withOutboundPartialDate(final LocalDate outboundPartialDate) {
    this.outboundPartialDate = outboundPartialDate;
    return this;
  }

  public CheapestQuotesRequestBuilder withOutboundPartialDate(final String outboundPartialDate) {
    this.outboundPartialDate = parse(outboundPartialDate);
    return this;
  }

  public CheapestQuotesRequestBuilder withInboundPartialDate(final LocalDate inboundPartialDate) {
    this.inboundPartialDate = inboundPartialDate;
    return this;
  }

  public CheapestQuotesRequestBuilder withInboundPartialDate(final String inboundPartialDate) {
    this.inboundPartialDate = parse(inboundPartialDate);
    return this;
  }

  public CheapestQuotesRequestBuilder withDefaultValues() {
    this.market = "GR";
    this.originCity = "ATH";
    this.destinationCountry = "ESP";
    this.currency = "EUR";
    this.locale = "en-GB";
    this.outboundPartialDate = parse("2016-10-10");
    this.inboundPartialDate = parse("2016-10-20");

    return this;
  }

}
