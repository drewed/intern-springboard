/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.service;

import org.familysearch.intern.springboard.ws.client.StandardsNameClient;
import org.familysearch.intern.springboard.ws.client.TreeNameClient;
import org.familysearch.intern.springboard.ws.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NameService {
  private static final Logger LOGGER = LoggerFactory.getLogger(NameService.class);
  private final StandardsNameClient standardsNameClient;
  private final TreeNameClient treeNameClient;

  public NameService(StandardsNameClient standardsNameClient, TreeNameClient treeNameClient) {
    this.standardsNameClient = standardsNameClient;
    this.treeNameClient = treeNameClient;
  }

  public Name standardizeName(String name, String languageTag) {
    Names names = standardsNameClient.segmentName(name, languageTag);
    if (names.names().isEmpty()) {
      throw new IllegalStateException("No standardized names returned for: " + name);
    }

    return names.names().getFirst();
  }

  public List<CountryStats> searchCountryStats(Name name, String languageTag) {
    // Extract surname fragments
    List<String> surnames = name.fragments().surname().stream()
        .map(Fragment::text)
        .toList();

    if (surnames.isEmpty()) {
      return List.of();
    }

    // Aggregate country stats from all surname searches
    Map<String, CountryStats> countryMap = new HashMap<>();
    for (String surname : surnames) {
      try {
        SurnamesStats stats = treeNameClient.nameSearch(surname, languageTag);
        for (SurnameStats surnameStats : stats.stats()) {
          for (CountryStats country : surnameStats.countries()) {
            countryMap.merge(
                country.code(),
                country,
                (existing, newStats) -> new CountryStats(
                    existing.name(),
                    existing.code(),
                    existing.count() + newStats.count()
                )
            );
          }
        }
      } catch (Exception e) {
        LOGGER.warn("Failed to search country stats for surname '{}' with language tag '{}': {}", surname, languageTag, e.getMessage());
      }
    }

    // Return top 3 by count
    return countryMap.values().stream()
        .sorted(Comparator.comparingLong(CountryStats::count).reversed())
        .limit(3)
        .collect(Collectors.toList());
  }
}
