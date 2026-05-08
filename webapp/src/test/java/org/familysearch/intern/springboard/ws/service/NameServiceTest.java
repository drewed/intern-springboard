/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.service;

import org.familysearch.intern.springboard.ws.client.StandardsNameClient;
import org.familysearch.intern.springboard.ws.client.TreeNameClient;
import org.familysearch.intern.springboard.ws.model.*;
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

  @Mock
  private TreeNameClient treeNameClient;

  private NameService nameService;

  @BeforeEach
  void setUp() {
    nameService = new NameService(standardsNameClient, treeNameClient);
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

  @Test
  void searchCountryStatsReturnsTop3Countries() {
    Fragment surnameFragment = new Fragment("Smith", "SURNAME_FRAG");
    Fragments fragments = new Fragments(java.util.List.of(), java.util.List.of(surnameFragment));
    Name name = new Name("John Smith", fragments);

    CountryStats us = new CountryStats("United States", "US", 1000);
    CountryStats uk = new CountryStats("United Kingdom", "UK", 800);
    CountryStats ca = new CountryStats("Canada", "CA", 600);
    CountryStats au = new CountryStats("Australia", "AU", 400);

    SurnameStats surnameStats = new SurnameStats("Smith", 2800, java.util.List.of(us, uk, ca, au));
    SurnamesStats stats = new SurnamesStats(java.util.List.of(surnameStats));

    when(treeNameClient.nameSearch("Smith", "en")).thenReturn(stats);

    List<CountryStats> result = nameService.searchCountryStats(name, "en");

    assertThat(result).hasSize(3);
    assertThat(result.get(0).code()).isEqualTo("US");
    assertThat(result.get(1).code()).isEqualTo("UK");
    assertThat(result.get(2).code()).isEqualTo("CA");
  }

  @Test
  void searchCountryStatsReturnsEmptyWhenNoFragments() {
    Name name = new Name("John Smith", new Fragments(java.util.List.of(), java.util.List.of()));

    List<CountryStats> result = nameService.searchCountryStats(name, "en");

    assertThat(result).isEmpty();
  }

  @Test
  void searchCountryStatsAggregatesMultipleSurnames() {
    Fragment surnameFragment1 = new Fragment("Smith", "SURNAME_FRAG");
    Fragment surnameFragment2 = new Fragment("Jones", "SURNAME_FRAG");
    Fragments fragments = new Fragments(java.util.List.of(), java.util.List.of(surnameFragment1, surnameFragment2));
    Name name = new Name("John Smith Jones", fragments);

    CountryStats us1 = new CountryStats("United States", "US", 1000);
    CountryStats us2 = new CountryStats("United States", "US", 500);

    SurnameStats smithStats = new SurnameStats("Smith", 1000, java.util.List.of(us1));
    SurnameStats jonesStats = new SurnameStats("Jones", 500, java.util.List.of(us2));

    when(treeNameClient.nameSearch("Smith", "en")).thenReturn(new SurnamesStats(java.util.List.of(smithStats)));
    when(treeNameClient.nameSearch("Jones", "en")).thenReturn(new SurnamesStats(java.util.List.of(jonesStats)));

    List<CountryStats> result = nameService.searchCountryStats(name, "en");

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().code()).isEqualTo("US");
    assertThat(result.getFirst().count()).isEqualTo(1500);
  }
}
