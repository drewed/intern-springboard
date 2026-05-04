/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Application {
  private Application() {
    // Prevent instantiation
  }

  static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
