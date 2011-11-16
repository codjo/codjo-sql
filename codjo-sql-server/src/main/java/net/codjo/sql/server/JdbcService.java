/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.Agent;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.Service;
import net.codjo.agent.ServiceException;
import net.codjo.agent.ServiceHelper;
/**
 * Service permettant l'accès à une base de données.
 */
public class JdbcService implements Service {
    private JdbcManager jdbcManager;


    public JdbcService(JdbcManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }


    public String getName() {
        return JdbcServiceHelper.NAME;
    }


    public void boot(ContainerConfiguration containerConfiguration) throws ServiceException {
    }


    public ServiceHelper getServiceHelper(Agent agent) {
        return new JdbcServiceHelper(jdbcManager);
    }
}
