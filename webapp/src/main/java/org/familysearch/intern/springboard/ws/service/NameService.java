/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.service;

import org.familysearch.intern.springboard.ws.client.StandardsNameClient;
import org.familysearch.intern.springboard.ws.model.Name;
import org.familysearch.intern.springboard.ws.model.Names;
import org.springframework.stereotype.Service;

@Service
public class NameService {
  private final StandardsNameClient standardsNameClient;

  public NameService(StandardsNameClient standardsNameClient) {
    this.standardsNameClient = standardsNameClient;
  }

  public Name standardizeName(String name, String languageTag) {
    Names names = standardsNameClient.segmentName(name, languageTag);
    if (names.names() == null || names.names().isEmpty()) {
      throw new IllegalStateException("No standardized names returned for: " + name);
    }
    return names.names().get(0);
  }
}
