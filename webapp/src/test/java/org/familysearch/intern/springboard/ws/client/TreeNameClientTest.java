/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.client;

import org.familysearch.intern.springboard.ws.exception.TreeNameSearchException;
import org.familysearch.intern.springboard.ws.model.SurnamesStats;
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

class TreeNameClientTest {

  private TreeNameClient client;
  private MockRestServiceServer mockServer;
  private RestClient.Builder builder;

  @BeforeEach
  void setUp() {
    builder = RestClient.builder();
    mockServer = MockRestServiceServer.bindTo(builder).build();
    client = new TreeNameClient("http://test.service", 100, 3, builder);
  }

  @Test
  void successfulNameSearch() {
    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Smith"))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Accept", "application/json"))
        .andExpect(header("Accept-Language", "en-US"))
        .andRespond(withSuccess("{\"stats\":[]}", MediaType.APPLICATION_JSON));

    SurnamesStats result = client.nameSearch("Smith", "en-US");

    assertThat(result).isNotNull();
    assertThat(result.stats()).isEmpty();
    mockServer.verify();
  }

  @Test
  void nonRetryableErrorThrowsException() {
    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Invalid"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withBadRequest());

    assertThatThrownBy(() -> client.nameSearch("Invalid", "en"))
        .isInstanceOf(TreeNameSearchException.class)
        .hasMessageContaining("Invalid")
        .hasMessageContaining("en");
  }

  @Test
  void verifyExceptionContainsSurnameAndLanguageTag() {
    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Garc%C3%ADa"))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("Accept-Language", "es-MX"))
        .andRespond(withServerError());

    assertThatThrownBy(() -> client.nameSearch("García", "es-MX"))
        .isInstanceOf(TreeNameSearchException.class)
        .satisfies(exception -> {
          TreeNameSearchException tse = (TreeNameSearchException) exception;
          assertThat(tse.getSurname()).isEqualTo("García");
          assertThat(tse.getLanguageTag()).isEqualTo("es-MX");
        });
  }

  @Test
  void retryTimingIsConfigurable() {
    TreeNameClient fastClient = new TreeNameClient("http://test.service", 50, 2, builder);

    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Test"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());
    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Test"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());
    mockServer.expect(requestTo("http://test.service/tree-surnames?surname=Test"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withTooManyRequests());

    long startTime = System.currentTimeMillis();
    assertThatThrownBy(() -> fastClient.nameSearch("Test", "en"))
        .isInstanceOf(TreeNameSearchException.class);
    long elapsed = System.currentTimeMillis() - startTime;

    assertThat(elapsed).isLessThan(300);
    mockServer.verify();
  }
}
