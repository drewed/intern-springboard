/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.acceptance;

import org.springframework.boot.SpringBootConfiguration;

import org.familysearch.stack.java.test.autoconfigure.acceptance.AcceptanceTest;

/**
 * Main Spring configuration class for all acceptance tests which is automatically loaded by using
 * {@link AcceptanceTest @AcceptanceTest} on the acceptance test class.
 * <p>
 * There is also a maven build profile which activates on the presence of the environment property and turns on the
 * running of the tests. This can be accomplished on the command line by executing:
 * <pre>
 *   mvn verify -Dacceptance.testing
 * </pre>
 * <p>
 * If you choose to run acceptance tests against another environment or system, you will also need to set the
 * `fs.test.acceptance.client.service-root` system property to the deployed application URL. See the URL used by the
 * validation pipeline.
 */
@SpringBootConfiguration
public class AcceptanceTestConfig {
  // create additional beans here for injection into any acceptance test, if needed.
}
