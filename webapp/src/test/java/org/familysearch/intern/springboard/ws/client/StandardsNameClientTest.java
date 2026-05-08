/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.client;

import org.familysearch.intern.springboard.ws.exception.NameSegmentationException;
import org.familysearch.intern.springboard.ws.model.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class StandardsNameClientTest {

  private StandardsNameClient client;
  private MockRestServiceServer mockServer;
  private RestClient.Builder builder;

  @BeforeEach
  void setUp() {
    builder = RestClient.builder();
    mockServer = MockRestServiceServer.bindTo(builder).build();
    client = new StandardsNameClient("http://test.service", 100, 3, builder);
  }

  @Test
  void successfulSegmentation() {
    mockServer.expect(requestTo("http://test.service/names?name=John%20Smith&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Language", "en-US"))
        .andExpect(header("Accept-Language", "en-US"))
        .andRespond(withSuccess("{\"names\":[]}", MediaType.APPLICATION_JSON));

    Names result = client.segmentName("John Smith", "en-US");

    assertThat(result).isNotNull();
    assertThat(result.names()).isEmpty();
    mockServer.verify();
  }

  @Test
  void nonRetryableErrorThrowsException() {
    mockServer.expect(requestTo("http://test.service/names?name=Invalid%20Name&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withBadRequest());

    assertThatThrownBy(() -> client.segmentName("Invalid Name", "en"))
        .isInstanceOf(NameSegmentationException.class)
        .hasMessageContaining("Invalid Name")
        .hasMessageContaining("en");
  }

  @Test
  void nullResponseThrowsException() {
    mockServer.expect(requestTo("http://test.service/names?name=Test%20Name&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess());

    assertThatThrownBy(() -> client.segmentName("Test Name", "fr"))
        .isInstanceOf(NameSegmentationException.class)
        .extracting(e -> ((NameSegmentationException) e).getName())
        .isEqualTo("Test Name");
  }

  @Test
  void verifyExceptionContainsNameAndLanguageTag() {
    mockServer.expect(requestTo("http://test.service/names?name=Mar%C3%ADa%20Garc%C3%ADa&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Language", "es-MX"))
        .andExpect(header("Accept-Language", "es-MX"))
        .andRespond(withServerError());

    assertThatThrownBy(() -> client.segmentName("María García", "es-MX"))
        .isInstanceOf(NameSegmentationException.class)
        .satisfies(exception -> {
          NameSegmentationException nse = (NameSegmentationException) exception;
          assertThat(nse.getName()).isEqualTo("María García");
          assertThat(nse.getLanguageTag()).isEqualTo("es-MX");
        });
  }

  @Test
  void retryTimingIsConfigurable() {
    // Test with faster retry timing (maxAttempts=2 means initial + 2 retries = 3 total calls)
    StandardsNameClient fastClient = new StandardsNameClient("http://test.service", 50, 2, builder);

    mockServer.expect(requestTo("http://test.service/names?name=Test&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());
    mockServer.expect(requestTo("http://test.service/names?name=Test&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());
    mockServer.expect(requestTo("http://test.service/names?name=Test&annotations=PROPER_CASE&details=true"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());

    long startTime = System.currentTimeMillis();
    assertThatThrownBy(() -> fastClient.segmentName("Test", "en"))
        .isInstanceOf(NameSegmentationException.class);
    long elapsed = System.currentTimeMillis() - startTime;

    // With initial delay 50ms: first attempt immediate, retry after 50ms, retry after 100ms = ~150ms total
    assertThat(elapsed).isLessThan(300);
    mockServer.verify();
  }
}
