/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.UserId;
import net.codjo.database.api.Database;
import net.codjo.database.api.DatabaseFactory;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.ConnectionPoolConfiguration;
import net.codjo.sql.server.JdbcManager;
import net.codjo.sql.server.JdbcServerException;
import net.codjo.sql.server.JdbcService;
import java.sql.SQLException;
import org.apache.log4j.Logger;
/**
 *
 */
public class JdbcServerPlugin implements ApplicationPlugin {
    private final Logger logger = Logger.getLogger(JdbcServerPlugin.class);
    public static final String DRIVER_PARAMETER = JdbcServerConfiguration.DRIVER_PARAMETER;
    public static final String URL_PARAMETER = JdbcServerConfiguration.URL_PARAMETER;
    public static final String CATALOG_PARAMETER = JdbcServerConfiguration.CATALOG_PARAMETER;
    private final ApplicationCore applicationCore;
    private JdbcServerOperationsImpl operations = new JdbcServerOperationsImpl();
    private JdbcServerConfiguration configuration = new JdbcServerConfiguration();


    public JdbcServerPlugin(ApplicationCore core) {
        this.applicationCore = core;
    }


    public JdbcServerConfiguration getConfiguration() {
        return configuration;
    }


    public JdbcServerOperations getOperations() {
        return operations;
    }


    public void initContainer(ContainerConfiguration containerConfiguration) throws JdbcServerException {

        JdbcServerConfiguration mergedConfig = configuration.merge(containerConfiguration);

        operations.setJdbcManager(createJdbcManager(mergedConfig.toConnectionPoolConfiguration()));
        applicationCore.addGlobalComponent(JdbcManager.class, operations.getJdbcManager());

        ensureLegacyCompatibility();

        applicationCore.addGlobalComponent(Database.class, DatabaseFactory.create(mergedConfig.getEngine()));

        containerConfiguration.addService(JdbcService.class.getName());
    }


    public void start(AgentContainer agentContainer) {
    }


    public void stop() {
        if (operations.getJdbcManager() != null) {
            for (UserId id : operations.getJdbcManager().getUserIds()) {
                try {
                    operations.destroyPool(id);
                }
                catch (JdbcServerException e) {
                    logger.warn("Impossible de supprimer le pool de connexion pour " + id.getLogin(), e);
                }
            }
        }
        applicationCore.removeGlobalComponent(JdbcManager.class);
        applicationCore.removeGlobalComponent(Database.class);
        removeLegacyCompatibility();
    }


    private JdbcManager createJdbcManager(ConnectionPoolConfiguration poolConfiguration)
          throws JdbcServerException {
        JdbcManager jdbcManager = new JdbcManager(poolConfiguration);
        logger.info("Activation de la connexion JDBC(" + poolConfiguration.getClassDriver()
                    + ") : " + poolConfiguration.getUrl() + "/" + poolConfiguration.getCatalog()
                    + " pour " + poolConfiguration.getApplicationName());
        return jdbcManager;
    }


    private void ensureLegacyCompatibility() {
        net.codjo.database.common.api.DatabaseFactory factory
              = new net.codjo.database.common.api.DatabaseFactory();
        applicationCore.addGlobalComponent(net.codjo.database.common.api.DatabaseFactory.class, factory);
        applicationCore.addGlobalComponent(net.codjo.database.common.api.DatabaseQueryHelper.class,
                                           factory.getDatabaseQueryHelper());
    }


    private void removeLegacyCompatibility() {
        applicationCore.removeGlobalComponent(net.codjo.database.common.api.DatabaseFactory.class);
        applicationCore.removeGlobalComponent(net.codjo.database.common.api.DatabaseQueryHelper.class);
    }


    private static class JdbcServerOperationsImpl implements JdbcServerOperations {
        private JdbcManager jdbcManager;


        public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException {
            return jdbcManager.createPool(userId, login, password);
        }


        public ConnectionPool getPool(UserId poolUserId) throws JdbcServerException {
            return jdbcManager.getPool(poolUserId);
        }


        public void destroyPool(UserId poolUserId) throws JdbcServerException {
            jdbcManager.destroyPool(poolUserId);
        }


        public JdbcManager getJdbcManager() {
            return jdbcManager;
        }


        public void setJdbcManager(JdbcManager jdbcManager) {
            this.jdbcManager = jdbcManager;
        }
    }
}