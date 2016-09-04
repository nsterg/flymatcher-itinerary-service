package com.flymatcher.itinerary.integration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.HttpStatus.SC_OK;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.flymatcher.itinerary.bootstrap.ServiceRunner;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ServiceRunner.class})
@WebIntegrationTest("server.port:0")
public class CheapestQuotesIntegrationTest {

  @Value("${local.server.port}")
  int port;

  @Value("${server.contextPath}")
  String contextPath;

  @Before
  public void beforeEachTest() {
    RestAssured.port = port;
  }

  @Test
  public void shouldReturnHappyResponse() throws IOException {

    // @formatter:off
    given().contentType(JSON)
        .queryParam("country", "GR").queryParam("city", "ATH")
        .queryParam("currency", "EUR")
        .queryParam("departureDate", "2016-01-01").when().get(buildRequestUrlStr()).then()
        .assertThat()
        .body(sameJSONAs(readFileToString(new File(
            "src/test/resources/integration/responses/cheapest-quotes-response-200.json"))))
        .statusCode(SC_OK);
    // @formatter:on
  }

  private String buildRequestUrlStr() {
    return "http://localhost:" + port + contextPath + "/v1/cheapest-quotes";
  }
}

