/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.acceptance.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import org.familysearch.stack.java.test.autoconfigure.acceptance.AcceptanceTest;

/**
 * Acceptance tests for SampleResource
 */
@AcceptanceTest(sessionEnabled = false)
class SampleResourcesSynchronousClientTest {
  @Autowired
  RestTestClient restTestClient;

  @Test
  void sampleEndpointUsingPost() {
    restTestClient.post()
                  .uri("/sample")
                  .contentType(MediaType.TEXT_PLAIN)
                  .body("World")
                  .exchange()
                  .expectStatus().is2xxSuccessful()
                  .expectBody().jsonPath("$.message").isEqualTo("Hello, World!");
  }

  @Test
  void sampleEndpointUsingGetAndQueryParam() {
    restTestClient.get()
                  .uri("/sample?name={name}", "Rodger")
                  .exchange()
                  .expectStatus().is2xxSuccessful()
                  .expectBody().jsonPath("$.message").isEqualTo("Hello, Rodger!");
  }
}
