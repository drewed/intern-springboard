/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.acceptance.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.client.RestTestClient;

import org.familysearch.stack.java.test.autoconfigure.acceptance.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Acceptance tests for Enunciate documentation
 */
@AcceptanceTest(sessionEnabled = false)
class EnunciateResourcesSynchronousClientTest {
  @Autowired
  RestTestClient restTestClient;

  @Test
  void enunciateDocsMountedAtRoot() {
    restTestClient.get()
                  .uri("/")
                  .exchange()
                  .expectStatus().is2xxSuccessful()
                  .expectBody(String.class)
                  .value(body -> assertThat(body)
                    .as("Expected Enunciate docs at the root." +
                        " If this test fails, please ensure 'enunciate:assemble' has been executed from the command" +
                        " line in the app module, or as a prebuild step in the IDE run configuration.")
                    .containsSubsequence("Resources", "SampleResource", "Enunciate"));
  }
}
