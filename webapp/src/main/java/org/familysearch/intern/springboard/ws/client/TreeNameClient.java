/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.client;

import org.familysearch.intern.springboard.ws.exception.RetryableException;
import org.familysearch.intern.springboard.ws.exception.TreeNameSearchException;
import org.familysearch.intern.springboard.ws.model.SurnamesStats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class TreeNameClient {
  private final RestClient restClient;
  private final int initialDelayMs;
  private final int maxAttempts;

  public TreeNameClient(
      @Value("${service.bindings.tree.name-search}") String baseUrl,
      @Value("${client.retry.initial-delay-ms}") int initialDelayMs,
      @Value("${client.retry.max-attempts}") int maxAttempts,
      RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    this.initialDelayMs = initialDelayMs;
    this.maxAttempts = maxAttempts;
  }

  public SurnamesStats nameSearch(String surname, String languageTag) {
    return executeWithRetry(surname, languageTag, 0);
  }

  private SurnamesStats executeWithRetry(String surname, String languageTag, int attempt) {
    try {
      return Objects.requireNonNull(restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/tree-surnames")
              .queryParam("surname", surname)
              .build())
          .header("Accept", "application/json")
          .header("Accept-Language", languageTag)
          .retrieve()
          .onStatus(status -> status.value() == 429 || status.value() == 503,
              (request, response) -> {
                throw new RetryableException();
              })
          .body(SurnamesStats.class));
    } catch (RetryableException e) {
      if (attempt < maxAttempts) {
        long delayMillis = (long) Math.pow(2, attempt) * initialDelayMs;
        try {
          Thread.sleep(delayMillis);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new TreeNameSearchException(surname, languageTag, ie);
        }

        return executeWithRetry(surname, languageTag, attempt + 1);
      }

      throw new TreeNameSearchException(surname, languageTag, e);
    } catch (Exception e) {
      throw new TreeNameSearchException(surname, languageTag, e);
    }
  }
}
