package com.flymatcher.itinerary.domain;

import static java.time.LocalDate.parse;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

import java.time.LocalDate;
import java.util.List;

import com.flymatcher.itinerary.exception.BadRequestException;

public class ItineraryRequest {

  private String market;

  private List<String> origins;

  private String currency;

  private String locale;

  private LocalDate outboundPartialDate;

  private LocalDate inboundPartialDate;


  public static ItineraryRequest valueOf(final String market, final String currency,
      final String locale, final List<String> origins, final String outboundPartialDate,
      final String inboundPartialDate) {
    final ItineraryRequest request = new ItineraryRequest();
    request.market = market;
    request.origins = origins;
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

  public List<String> getOrigins() {
    return origins;
  }

  public void setOrigins(final List<String> origins) {
    this.origins = origins;
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
