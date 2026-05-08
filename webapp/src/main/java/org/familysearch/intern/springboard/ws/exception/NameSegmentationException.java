/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.exception;

public class NameSegmentationException extends RuntimeException {
  private final String name;
  private final String languageTag;

  public NameSegmentationException(String name, String languageTag, Throwable cause) {
    super("Failed to segment name '%s' with language tag '%s'".formatted(name, languageTag), cause);
    this.name = name;
    this.languageTag = languageTag;
  }

  public String getName() {
    return name;
  }

  public String getLanguageTag() {
    return languageTag;
  }
}
