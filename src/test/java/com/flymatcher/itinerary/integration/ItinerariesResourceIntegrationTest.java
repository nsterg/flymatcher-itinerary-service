package com.flymatcher.itinerary.integration;

import static com.github.restdriver.clientdriver.ClientDriverRequest.Method.GET;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.flymatcher.itinerary.bootstrap.ServiceRunner;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceRunner.class})
@TestPropertySource(locations = "classpath:/integration/test-application.properties")
@WebIntegrationTest("server.port:0")
public class ItinerariesResourceIntegrationTest {

  @Value("${local.server.port}")
  int port;

  @Value("${server.contextPath}")
  String contextPath;

  @Rule
  public ClientDriverRule driver = new ClientDriverRule(7001);

  @Before
  public void beforeEachTest() {
    RestAssured.port = port;
  }

  @Test
  public void shouldReturnFlightMatchesResponse() throws IOException {

    final String skyscannerAdaptorResponse1 = readFileToString(new File(
        "src/test/resources/integration/adaptor-responses/skyscanner-adaptor-response-1.json"));
    final String skyscannerAdaptorResponse2 = readFileToString(new File(
        "src/test/resources/integration/adaptor-responses/skyscanner-adaptor-response-2.json"));
    final String skyscannerAdaptorResponse3 = readFileToString(new File(
        "src/test/resources/integration/adaptor-responses/skyscanner-adaptor-response-3.json"));
    final String skyscannerAdaptorResponse4 = readFileToString(new File(
        "src/test/resources/integration/adaptor-responses/skyscanner-adaptor-response-4.json"));

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/ATH/IT/2016-10-10/2016-10-20").withMethod(
            GET),
        giveResponse(skyscannerAdaptorResponse1, "application/json").withStatus(200));

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/MAD/IT/2016-10-10/2016-10-20").withMethod(
            GET),
        giveResponse(skyscannerAdaptorResponse2, "application/json").withStatus(200));

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/ATH/FR/2016-10-10/2016-10-20").withMethod(
            GET),
        giveResponse(skyscannerAdaptorResponse3, "application/json").withStatus(200));

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/MAD/FR/2016-10-10/2016-10-20").withMethod(
            GET),
        giveResponse(skyscannerAdaptorResponse4, "application/json").withStatus(200));

    // @formatter:off
    given()
        .accept(JSON)
          .contentType(JSON)
          .pathParameters(aParameterNameValuePairs())
        .when()
          .get(buildRequestUrlStr())
        .then()
        .assertThat()
          .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/flymatcher-responses/flight-match-response-200.json"))))
        .statusCode(SC_OK);
    // @formatter:on
  }

  @Test
  public void shouldReturnBadRequestError() throws IOException {

    // @formatter:off
    given()
        .accept(JSON)
          .contentType(JSON)
          .pathParameters(aParameterNameValuePairsWithWrongDate())
        .when()
          .get(buildRequestUrlStr())
        .then()
        .assertThat()
          .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/flymatcher-responses/flight-match-response-400.json"))))
        .statusCode(SC_BAD_REQUEST);
    // @formatter:on
  }

  @Test
  public void shouldReturnBadRequestErrorWhenSkyscannerAdaptorReturns() throws IOException {

    final String skyscannerAdaptorResponse = readFileToString(new File(
        "src/test/resources/integration/adaptor-responses/skyscanner-adaptor-400-response.json"));

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/ATH/IT/2016-10-10/2016-10-20").withMethod(
            GET),
        giveResponse(skyscannerAdaptorResponse, "application/json").withStatus(400));

    // @formatter:off
    given()
        .accept(JSON)
          .contentType(JSON)
          .pathParameters(aParameterNameValuePairs())
        .when()
          .get(buildRequestUrlStr())
        .then()
        .assertThat()
          .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/flymatcher-responses/flight-match-response-400-skyscanner-error.json"))))
        .statusCode(SC_BAD_REQUEST);
    // @formatter:on
  }

  @Test
  public void shouldReturnInternalServerErrorWhenSkyscannerAdaptorReturnsNonJson()
      throws IOException {

    driver.addExpectation(
        onRequestTo("/v1/cheapest-quotes/GR/EUR/en-GB/ATH/IT/2016-10-10/2016-10-20")
            .withMethod(GET),
        giveResponse("<html>Some nexpected error</html>", "application/json").withStatus(500));

    // @formatter:off
    given()
        .accept(JSON)
          .contentType(JSON)
          .pathParameters(aParameterNameValuePairs())
        .when()
          .get(buildRequestUrlStr())
        .then()
        .assertThat()
          .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/flymatcher-responses/flight-match-response-500-skyscanner-error.json"))))
        .statusCode(SC_INTERNAL_SERVER_ERROR);
    // @formatter:on
  }


  private Map<String, Object> aParameterNameValuePairs() {
    final Map<String, Object> map = new HashMap<>();
    map.put("market", "GR");
    map.put("currency", "EUR");
    map.put("locale", "en-GB");
    map.put("origins", "ATH,MAD");
    map.put("outboundDate", "2016-10-10");
    map.put("inboundDate", "2016-10-20");

    return map;
  }

  private Map<String, Object> aParameterNameValuePairsWithWrongDate() {
    final Map<String, Object> map = aParameterNameValuePairs();
    map.put("inboundDate", "XXX");

    return map;
  }

  private String buildRequestUrlStr() {
    return "http://localhost:" + port + contextPath
        + "/v1/itineraries/{market}/{currency}/{locale}/{origins}/{outboundDate}/{inboundDate}";
  }
}

