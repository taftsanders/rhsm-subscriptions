/*
 * Copyright Red Hat, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Red Hat trademarks are not licensed under GPLv3. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.candlepin.subscriptions.clowder;

import static org.candlepin.subscriptions.clowder.ClowderJsonEnvironmentPostProcessor.JSON_RESOURCE_LOCATION;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;

class ClowderJsonEnvironmentPostProcessorTest {
  private ClowderJsonEnvironmentPostProcessor postProcessor;

  private ConfigurableEnvironment environment = new StandardEnvironment();
  private final DeferredLogFactory logFactory = Supplier::get;

  @BeforeEach
  public void setUp() {
    postProcessor =
        new ClowderJsonEnvironmentPostProcessor(
            logFactory.getLog(ClowderJsonEnvironmentPostProcessor.class));
  }

  @Test
  void readsClowderJsonTest() {
    addClowderJson();

    postProcessor.postProcessEnvironment(environment, null);
    assertEquals(
        "env-rhsm-kafka.rhsm.svc", environment.getProperty("clowder.kafka.brokers[0].hostname"));
    assertEquals("rhsm-db", environment.getProperty("clowder.database.name"));
  }

  @Test
  void handlesDefaults() {
    addClowderJson();
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        environment, "testDefault=${clowder.notFound:default}");
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        environment, "testNoDefault=${clowder.database.name:defaultDbName}");
    postProcessor.postProcessEnvironment(environment, null);

    assertEquals("default", environment.getProperty("testDefault"));
    assertEquals("rhsm-db", environment.getProperty("testNoDefault"));
  }

  private void addClowderJson() {
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        environment, JSON_RESOURCE_LOCATION + "=classpath:test-clowder-config.json");
  }
}
