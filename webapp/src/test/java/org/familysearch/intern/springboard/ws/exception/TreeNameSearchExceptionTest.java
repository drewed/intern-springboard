/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TreeNameSearchExceptionTest {

  @Test
  void exceptionContainsSurnameAndLanguageTag() {
    String surname = "Smith";
    String languageTag = "en-US";
    Exception cause = new RuntimeException("Network error");

    TreeNameSearchException exception = new TreeNameSearchException(surname, languageTag, cause);

    assertThat(exception.getSurname()).isEqualTo(surname);
    assertThat(exception.getLanguageTag()).isEqualTo(languageTag);
    assertThat(exception.getCause()).isEqualTo(cause);
    assertThat(exception.getMessage()).contains(surname).contains(languageTag);
  }

  @Test
  void exceptionMessageIsDescriptive() {
    TreeNameSearchException exception = new TreeNameSearchException(
        "García",
        "es",
        new IllegalStateException("Service unavailable")
    );

    assertThat(exception.getMessage())
        .contains("Failed to search tree names")
        .contains("García")
        .contains("es");
  }
}
