/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.Agent;
import net.codjo.agent.ServiceException;
import net.codjo.agent.ServiceHelper;
import net.codjo.agent.UserId;
import java.sql.SQLException;
/**
 * {@link ServiceHelper} utilisé par les agents pour accéder à la base.
 */
public class JdbcServiceHelper implements ServiceHelper {
    public static final String NAME = "JDBCService.NAME";
    private JdbcManager manager;


    public JdbcServiceHelper(JdbcManager manager) {
        this.manager = manager;
    }


    public ConnectionPool createPool(UserId userId, String login, String password)
          throws ServiceException, SQLException {
        return manager.createPool(userId, login, password);
    }


    public ConnectionPool getPool(UserId userId) throws ServiceException {
        try {
            return manager.getPool(userId);
        }
        catch (JdbcServerException e) {
            throw new ServiceException(e);
        }
    }


    public void destroyPool(UserId userId) throws ServiceException {
        try {
            manager.destroyPool(userId);
        }
        catch (JdbcServerException e) {
            throw new ServiceException(e);
        }
    }


    public void init(Agent agent) {
    }
}
