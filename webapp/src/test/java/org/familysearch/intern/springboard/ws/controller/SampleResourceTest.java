/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
                                "message": "Hello, testing!"
                              }
                              """);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getSampleWithMissingName(String name) {
    assertThat(mockMvc.get().uri("/sample")
                      .queryParam("name", name))
      .hasStatusOk()
      .bodyJson().isEqualTo("""
                              {
                                "message": "Hello, World!"
                              }
                              """);
  }

  @Test
  void updateSample() {
    assertThat(mockMvc.post().uri("/sample")
                      .contentType(TEXT_PLAIN)
                      .content("testing"))
      .hasStatusOk()
      .bodyJson().isEqualTo("""
                              {
                                "message": "Hello, testing!"
                              }
                              """);
  }

}
