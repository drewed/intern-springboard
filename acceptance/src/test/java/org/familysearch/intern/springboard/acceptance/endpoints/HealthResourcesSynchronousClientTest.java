/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.acceptance.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.client.RestTestClient;

import org.familysearch.stack.java.test.autoconfigure.acceptance.AcceptanceTest;

/**
 * Acceptance tests for health endpoints
 */
@AcceptanceTest(sessionEnabled = false)
class HealthResourcesSynchronousClientTest {
  @Autowired
  RestTestClient restTestClient;

  @Test
  void httpGetHealthCheckHeartbeat() {
    restTestClient.get()
                  .uri("/healthcheck/heartbeat")
                  .exchange()
                  .expectStatus().isOk();
  }

  @Test
  void httpGetHealthCheckVitals() {
    restTestClient.get()
                  .uri("/healthcheck/vitals")
                  .exchange()
                  .expectStatus().isOk();
  }
}
