/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.exception;

/**
 * Exception thrown internally to signal that a request should be retried.
 * Used by client classes to trigger retry logic for transient failures like 429 or 503 responses.
 */
public class RetryableException extends RuntimeException {
  public RetryableException() {
    super();
  }

  public RetryableException(String message) {
    super(message);
  }

  public RetryableException(String message, Throwable cause) {
    super(message, cause);
  }
}
