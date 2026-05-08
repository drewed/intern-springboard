/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import org.familysearch.intern.springboard.ws.model.Name;
import org.familysearch.intern.springboard.ws.service.NameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@WebMvcTest(SampleResource.class)
class SampleResourceTest {

  @Autowired
  MockMvcTester mockMvc;

  @Autowired
  NameService nameService;

  @TestConfiguration
  static class TestConfig {
    @Bean
    @Primary
    public NameService nameService() {
      return Mockito.mock(NameService.class);
    }
  }

  @Test
  void getSampleWithName() {
    Name standardizedName = new Name("Testing Standardized", new org.familysearch.intern.springboard.ws.model.Fragments(java.util.List.of(), java.util.List.of()));
    when(nameService.standardizeName("testing", "en"))
        .thenReturn(standardizedName);
    when(nameService.searchCountryStats(standardizedName, "en"))
        .thenReturn(java.util.List.of());

    assertThat(mockMvc.get().uri("/sample")
        .queryParam("name", "testing"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {
              "greeting": "Hello",
              "fullName": "Testing Standardized"
            }
            """);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void getSampleWithMissingName(String name) {
    assertThat(mockMvc.get().uri("/sample").queryParam("name", name))
        .hasStatus(HttpStatus.BAD_REQUEST)
        .extracting("response.errorMessage")
        .isNotNull()
        .asString()
        .matches("Required parameter 'name' is (not present|blank)\\.");
  }

  @Test
  void updateSample() {
    Name standardizedName = new Name("Testing Standardized", new org.familysearch.intern.springboard.ws.model.Fragments(java.util.List.of(), java.util.List.of()));
    when(nameService.standardizeName("testing", "en"))
        .thenReturn(standardizedName);
    when(nameService.searchCountryStats(standardizedName, "en"))
        .thenReturn(java.util.List.of());

    assertThat(mockMvc.post().uri("/sample")
        .contentType(TEXT_PLAIN)
        .content("testing"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {
              "greeting": "Hello",
              "fullName": "Testing Standardized"
            }
            """);
  }

  @Test
  void getSampleWithCustomLanguage() {
    Name standardizedName = new Name("María García", new org.familysearch.intern.springboard.ws.model.Fragments(java.util.List.of(), java.util.List.of()));
    when(nameService.standardizeName("María García", "es-MX"))
        .thenReturn(standardizedName);
    when(nameService.searchCountryStats(standardizedName, "es-MX"))
        .thenReturn(java.util.List.of());

    assertThat(mockMvc.get().uri("/sample")
        .queryParam("name", "María García")
        .header("Accept-Language", "es-MX"))
        .hasStatusOk()
        .bodyJson().isEqualTo("""
            {
              "greeting": "Hello",
              "fullName": "María García"
            }
            """);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void updateSampleWithMissingName(String name) {
    MockMvcTester.MockMvcRequestBuilder builder = mockMvc.post()
        .uri("/sample")
        .contentType(TEXT_PLAIN);
    if (name != null) {
      builder.content(name);
    }

    assertThat(builder)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .extracting("response.errorMessage")
        .isNotNull()
        .asString()
        .matches("Required parameter 'name' is (not present|blank)\\.");
  }
}
