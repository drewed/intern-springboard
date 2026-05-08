/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import jakarta.validation.constraints.NotBlank;
import org.familysearch.intern.springboard.ws.model.Message;
import org.familysearch.intern.springboard.ws.model.Name;
import org.familysearch.intern.springboard.ws.service.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sample")
public class SampleResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(SampleResource.class);
  private final NameService nameService;

  public SampleResource(NameService nameService) {
    this.nameService = nameService;
  }

  private ResponseEntity<Message> returnResponseEntity(final String name, final String languageTag) {
    LOGGER.info("Received request with '{}' and language tag '{}'.", name, languageTag);
    Name standardizedName = nameService.standardizeName(name, languageTag);
    return ResponseEntity.ok(new Message(standardizedName.fullName()));
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> getSample(
      @RequestParam @NotBlank(message = "Required parameter 'name' is blank.") String name,
      @RequestHeader(value = "Accept-Language", defaultValue = "en") @NotBlank String acceptLanguage) {
    return returnResponseEntity(name, acceptLanguage);
  }

  @PostMapping(
    consumes = MediaType.TEXT_PLAIN_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Message> updateSample(
      @RequestBody @NotBlank(message = "Required parameter 'name' is blank.") String input,
      @RequestHeader(value = "Accept-Language", defaultValue = "en") @NotBlank String acceptLanguage) {
    return returnResponseEntity(input, acceptLanguage);
  }
}
