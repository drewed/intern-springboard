/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.model;

import org.jspecify.annotations.Nullable;

public record Message (
    @Nullable String greeting,
    String fullName) {
  public Message {
    if (greeting == null) {
      greeting = "Hello";
    }
  }

  public Message(String fullName) {
    this(null, fullName);
  }
}
