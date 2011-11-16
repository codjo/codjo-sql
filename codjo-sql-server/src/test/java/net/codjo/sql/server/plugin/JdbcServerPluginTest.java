/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.ContainerConfigurationMock;
import net.codjo.database.api.Database;
import net.codjo.database.api.Engine;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.sql.server.JdbcManager;
import net.codjo.sql.server.JdbcServerException;
import net.codjo.test.common.LogString;
import junit.framework.TestCase;
/**
 * Classe de test de {@link JdbcServerPlugin}.
 */
public class JdbcServerPluginTest extends TestCase {
    private JdbcServerPlugin plugin;
    private LogString log = new LogString();
    private ApplicationCore applicationCore;


    public void test_init_addService() throws Exception {
        ContainerConfiguration configuration = createEmptyConfiguration();

        plugin.initContainer(configuration);

        log.assertContent("addService(net.codjo.sql.server.JdbcService)");
    }


    public void test_globalComponent_jdbcManager() throws Exception {
        assertGlobalComponentExists(JdbcManager.class);
    }


    public void test_database_default() throws Exception {
        assertEquals(Engine.SYBASE, plugin.getConfiguration().getEngine());
    }


    public void test_database_configuration() throws Exception {
        ContainerConfiguration configuration = createEmptyConfiguration();
        configuration.setParameter(JdbcServerConfiguration.ENGINE_PARAMETER, "unknown");

        try {
            plugin.initContainer(configuration);
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().contains("Engine.UNKNOWN"));
        }
    }


    public void test_globalComponent_database() throws Exception {
        assertGlobalComponentExists(Database.class);
    }


    public void test_globalComponent_legacyDatabaseFactory() throws Exception {
        assertGlobalComponentExists(net.codjo.database.common.api.DatabaseFactory.class);
    }


    public void test_globalComponent_legacyDatabaseQueryHelper() throws Exception {
        assertGlobalComponentExists(net.codjo.database.common.api.DatabaseQueryHelper.class);
    }


    private <T> void assertGlobalComponentExists(Class<T> componentClass) throws JdbcServerException {
        plugin.initContainer(createEmptyConfiguration());

        assertNotNull(applicationCore.getGlobalComponent(componentClass));

        plugin.stop();

        assertNull(applicationCore.getGlobalComponent(componentClass));
    }


    public void test_other() throws Exception {
        plugin.start(null);
        plugin.stop();
        assertNotNull(plugin.getConfiguration());
        assertNotNull(plugin.getOperations());
    }


    @Override
    protected void setUp() throws Exception {
        applicationCore = new ApplicationCore() {
            @Override
            protected AgentContainer createAgentContainer(ContainerConfiguration configuration) {
                return null;
            }
        };
        plugin = new JdbcServerPlugin(applicationCore);
    }


    private ContainerConfiguration createEmptyConfiguration() {
        ContainerConfiguration configuration = new ContainerConfigurationMock(log);
        configuration.setParameter(JdbcServerConfiguration.DRIVER_PARAMETER, "fakedb.FakeDriver");
        configuration.setParameter(JdbcServerConfiguration.URL_PARAMETER, "");
        configuration.setParameter(JdbcServerConfiguration.CATALOG_PARAMETER, "");
        return configuration;
    }
}
