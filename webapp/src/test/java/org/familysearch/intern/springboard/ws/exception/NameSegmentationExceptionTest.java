/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameSegmentationExceptionTest {

  @Test
  void exceptionContainsNameAndLanguageTag() {
    String name = "John Smith";
    String languageTag = "en-US";
    Exception cause = new RuntimeException("Network error");

    NameSegmentationException exception = new NameSegmentationException(name, languageTag, cause);

    assertThat(exception.getName()).isEqualTo(name);
    assertThat(exception.getLanguageTag()).isEqualTo(languageTag);
    assertThat(exception.getCause()).isEqualTo(cause);
    assertThat(exception.getMessage()).contains(name).contains(languageTag);
  }

  @Test
  void exceptionMessageIsDescriptive() {
    NameSegmentationException exception = new NameSegmentationException(
        "Jane Doe",
        "es",
        new IllegalStateException("Service unavailable")
    );

    assertThat(exception.getMessage())
        .contains("Failed to segment name")
        .contains("Jane Doe")
        .contains("es");
  }
}
