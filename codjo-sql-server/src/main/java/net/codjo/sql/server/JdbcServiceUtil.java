/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.ServiceException;
import java.sql.SQLException;
/**
 * Classe utilitaire permettant d'acceder facilement au ConnectionPool.
 */
public class JdbcServiceUtil {
    public ConnectionPool getConnectionPool(Agent agent, AclMessage message)
          throws SQLException {
        try {
            JdbcServiceHelper helper =
                  (JdbcServiceHelper)agent.getHelper(JdbcServiceHelper.NAME);
            return helper.getPool(message.decodeUserId());
        }
        catch (ServiceException exception) {
            SQLException sqlException =
                  new SQLException("Impossible d'acceder au service JDBC : "
                                   + exception.getLocalizedMessage());
            sqlException.initCause(exception);
            throw sqlException;
        }
    }
}
