/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.client;

import org.familysearch.intern.springboard.ws.exception.NameSegmentationException;
import org.familysearch.intern.springboard.ws.exception.RetryableException;
import org.familysearch.intern.springboard.ws.model.Names;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class StandardsNameClient {
  private final RestClient restClient;
  private final int initialDelayMs;
  private final int maxAttempts;

  public StandardsNameClient(
      @Value("${service.bindings.standards.name-seg}") String baseUrl,
      @Value("${client.retry.initial-delay-ms}") int initialDelayMs,
      @Value("${client.retry.max-attempts}") int maxAttempts,
      RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    this.initialDelayMs = initialDelayMs;
    this.maxAttempts = maxAttempts;
  }

  public Names segmentName(String name, String languageTag) {
    return executeWithRetry(name, languageTag, 0);
  }

  private Names executeWithRetry(String name, String languageTag, int attempt) {
    try {
      return Objects.requireNonNull(restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/names")
              .queryParam("fullname", name)
              .queryParam("annotations", "PROPER_CASE")
              .queryParam("details", "true")
              .build())
          .header("Accept", "application/json")
          .header("Language", languageTag)
          .header("Accept-Language", languageTag)
          .retrieve()
          .onStatus(status -> status.value() == 429 || status.value() == 503,
              (request, response) -> {
                throw new RetryableException();
              })
          .body(Names.class));
    } catch (RetryableException e) {
      if (attempt < maxAttempts) {
        long delayMillis = (long) Math.pow(2, attempt) * initialDelayMs;
        try {
          Thread.sleep(delayMillis);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new NameSegmentationException(name, languageTag, ie);
        }

        return executeWithRetry(name, languageTag, attempt + 1);
      }
      
      throw new NameSegmentationException(name, languageTag, e);
    } catch (Exception e) {
      throw new NameSegmentationException(name, languageTag, e);
    }
  }
}
