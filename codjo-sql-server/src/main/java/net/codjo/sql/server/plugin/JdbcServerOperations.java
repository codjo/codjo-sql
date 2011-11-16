package net.codjo.sql.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.JdbcServerException;
import java.sql.SQLException;
/**
 *
 */
public interface JdbcServerOperations {
    public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException;


    public ConnectionPool getPool(UserId poolUserId) throws JdbcServerException;


    public void destroyPool(UserId poolUserId) throws JdbcServerException;
}
