/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.model;

public class Message {
  private final String messageString;

  public Message(String name) {
    messageString = "Hello, %s!".formatted(name);
  }

  public String getMessage() {
    return messageString;
  }
}
