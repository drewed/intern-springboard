/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import jakarta.validation.constraints.NotBlank;
import org.familysearch.intern.springboard.ws.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sample")
public class SampleResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(SampleResource.class);

  private ResponseEntity<Message> returnResponseEntity(final String name) {
    LOGGER.info("Received request with '{}'.", name);
    return ResponseEntity.ok(new Message(name));
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> getSample(@RequestParam @NotBlank(message = "Required parameter 'name' is blank.") String name) {
    return returnResponseEntity(name);
  }

  @PostMapping(
    consumes = MediaType.TEXT_PLAIN_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> updateSample(@RequestBody @NotBlank(message = "Required parameter 'name' is blank.") String input) {
    return returnResponseEntity(input);
  }
}
