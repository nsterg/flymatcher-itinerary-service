package com.flymatcher.itinerary.domain;

import static java.time.LocalDate.parse;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

import java.time.LocalDate;

import com.flymatcher.itinerary.exception.BadRequestException;

public class CheapestQuotesRequest {

  private String market;

  private String originCity;

  private String currency;

  private String locale;

  private String destinationCountry;

  private LocalDate outboundPartialDate;

  private LocalDate inboundPartialDate;

  public static CheapestQuotesRequest valueOf(final String market, final String currency,
      final String locale, final String originCity, final String destinationCountry,
      final String outboundPartialDate, final String inboundPartialDate) {
    final CheapestQuotesRequest request = new CheapestQuotesRequest();
    request.market = market;
    request.originCity = originCity;
    request.destinationCountry = destinationCountry;
    request.currency = currency;
    request.locale = locale;
    try {
      request.outboundPartialDate = parse(outboundPartialDate);
      request.inboundPartialDate = parse(inboundPartialDate);
    } catch (final Exception e) {
      throw new BadRequestException("Request date was not in the expected format: yyyy-mm-dd");
    }
    return request;
  }

  public String getMarket() {
    return market;
  }

  public void setMarket(final String market) {
    this.market = market;
  }

  public String getOriginCity() {
    return originCity;
  }

  public void setOriginCity(final String city) {
    this.originCity = city;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(final String currency) {
    this.currency = currency;
  }


  public String getLocale() {
    return locale;
  }

  public void setLocale(final String locale) {
    this.locale = locale;
  }

  public String getDestinationCountry() {
    return destinationCountry;
  }

  public void setDestinationCountry(final String destinationCountry) {
    this.destinationCountry = destinationCountry;
  }

  public LocalDate getOutboundPartialDate() {
    return outboundPartialDate;
  }

  public void setOutboundPartialDate(final LocalDate outboundPartialDate) {
    this.outboundPartialDate = outboundPartialDate;
  }

  public LocalDate getInboundPartialDate() {
    return inboundPartialDate;
  }

  public void setInboundPartialDate(final LocalDate inboundPartialDate) {
    this.inboundPartialDate = inboundPartialDate;
  }

  @Override
  public int hashCode() {
    return reflectionHashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return reflectionToString(this);
  }

}
