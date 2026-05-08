/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message (
    @Nullable String greeting,
    String fullName,
    @Nullable List<CountryStats> countries) {
  public Message {
    if (greeting == null) {
      greeting = "Hello";
    }
  }

  public Message(String fullName, @Nullable List<CountryStats> countries) {
    this(null, fullName, countries);
  }
}
