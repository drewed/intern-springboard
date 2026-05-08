/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@WebMvcTest(SampleResource.class)
class SampleResourceTest {

  @Autowired
  MockMvcTester mockMvc;

  @Test
  void getSampleWithName() {
    assertThat(mockMvc.get().uri("/sample")
        .queryParam("name", "testing"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {
              "greeting": "Hello",
              "fullName": "testing"
            }
            """);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void getSampleWithMissingName(String name) {
    assertThat(mockMvc.get().uri("/sample").queryParam("name", name))
        .hasStatus(HttpStatus.BAD_REQUEST)
        .extracting("response.errorMessage")
        .isNotNull()
        .asString()
        .matches("Required parameter 'name' is (not present|blank)\\.");
  }

  @Test
  void updateSample() {
    assertThat(mockMvc.post().uri("/sample")
        .contentType(TEXT_PLAIN)
        .content("testing"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {
              "greeting": "Hello",
              "fullName": "testing"
            }
            """);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void updateSampleWithMissingName(String name) {
    MockMvcTester.MockMvcRequestBuilder builder = mockMvc.post()
        .uri("/sample")
        .contentType(TEXT_PLAIN);
    if (name != null) {
      builder.content(name);
    }

    assertThat(builder)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .extracting("response.errorMessage")
        .isNotNull()
        .asString()
        .matches("Required parameter 'name' is (not present|blank)\\.");
  }
}
