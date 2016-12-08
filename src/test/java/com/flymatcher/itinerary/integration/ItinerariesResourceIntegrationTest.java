package com.flymatcher.itinerary.integration;

import static com.github.restdriver.clientdriver.ClientDriverRequest.Method.GET;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.HttpStatus.SC_OK;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.File;
import java.io.IOException;

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

    driver.addExpectation(onRequestTo("/v1/cheapest-quotes/").withMethod(GET),
        giveResponse(skyscannerAdaptorResponse1, "application/json").withStatus(200));

    driver.addExpectation(onRequestTo("/v1/cheapest-quotes/").withMethod(GET),

        giveResponse(skyscannerAdaptorResponse2, "application/json").withStatus(200));

    driver.addExpectation(onRequestTo("/v1/cheapest-quotes/").withMethod(GET),
        giveResponse(skyscannerAdaptorResponse3, "application/json").withStatus(200));

    driver.addExpectation(onRequestTo("/v1/cheapest-quotes/").withMethod(GET),

        giveResponse(skyscannerAdaptorResponse4, "application/json").withStatus(200));

    // @formatter:off
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("origins", asList("Athens International", "Madrid")).queryParam("inboundDate", "2016-10-10")
        .queryParam("outboundDate", "2016-10-20").when().get(buildRequestUrlStr()).then()
        .assertThat()
        .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/flymatcher-responses/flight-match-response-200.json"))))
        .statusCode(SC_OK);
    // @formatter:on
  }

  private String buildRequestUrlStr() {
    return "http://localhost:" + port + contextPath + "/v1/itineraries";
  }
}

