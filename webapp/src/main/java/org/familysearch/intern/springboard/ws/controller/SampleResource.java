/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.familysearch.intern.springboard.ws.model.Message;

@RestController
@RequestMapping("sample")
public class SampleResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(SampleResource.class);

  private ResponseEntity<Message> returnResponseEntity(final String input) {
    LOGGER.info("Received request with '{}'.", input);

    String name = Optional.of(input)
                          .filter(StringUtils::hasLength)
                          .orElse("World");

    return ResponseEntity.ok(new Message(name));
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> getSample(@RequestParam(required = false) String name) {
    return returnResponseEntity(name);
  }

  @PostMapping(
    consumes = MediaType.TEXT_PLAIN_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> updateSample(@RequestBody String input) {
    return returnResponseEntity(input);
  }
}
