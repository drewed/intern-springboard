/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.service;

import org.familysearch.intern.springboard.ws.client.StandardsNameClient;
import org.familysearch.intern.springboard.ws.model.Name;
import org.familysearch.intern.springboard.ws.model.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameServiceTest {

  @Mock
  private StandardsNameClient standardsNameClient;

  private NameService nameService;

  @BeforeEach
  void setUp() {
    nameService = new NameService(standardsNameClient);
  }

  @Test
  void standardizeNameReturnsFirstName() {
    Name expectedName = new Name("John Smith", new org.familysearch.intern.springboard.ws.model.Fragments(java.util.List.of(), java.util.List.of()));
    Names names = new Names(List.of(expectedName));
    when(standardsNameClient.segmentName("john smith", "en-US")).thenReturn(names);

    Name result = nameService.standardizeName("john smith", "en-US");

    assertThat(result).isEqualTo(expectedName);
    assertThat(result.fullName()).isEqualTo("John Smith");
  }

  @Test
  void standardizeNameThrowsExceptionWhenNoNamesReturned() {
    Names emptyNames = new Names(List.of());
    when(standardsNameClient.segmentName("invalid", "en")).thenReturn(emptyNames);

    assertThatThrownBy(() -> nameService.standardizeName("invalid", "en"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No standardized names returned");
  }

  @Test
  void standardizeNameThrowsExceptionWhenEmptyList() {
    Names emptyNames2 = new Names(List.of());
    when(standardsNameClient.segmentName("test", "fr")).thenReturn(emptyNames2);

    assertThatThrownBy(() -> nameService.standardizeName("test", "fr"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No standardized names returned");
  }
}
