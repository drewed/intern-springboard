/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.exception;

public class TreeNameSearchException extends RuntimeException {
  private final String surname;
  private final String languageTag;

  public TreeNameSearchException(String surname, String languageTag, Throwable cause) {
    super("Failed to search tree names for surname '%s' with language tag '%s'".formatted(surname, languageTag), cause);
    this.surname = surname;
    this.languageTag = languageTag;
  }

  public String getSurname() {
    return surname;
  }

  public String getLanguageTag() {
    return languageTag;
  }
}
