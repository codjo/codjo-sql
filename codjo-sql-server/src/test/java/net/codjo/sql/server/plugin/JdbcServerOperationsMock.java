package net.codjo.sql.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.ConnectionPoolMock;
import net.codjo.sql.server.JdbcServerException;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
/**
 *
 */
public class JdbcServerOperationsMock implements JdbcServerOperations {
    private LogString log;
    private UserId userIdMock;
    private ConnectionPoolMock connectionPoolMock;


    public JdbcServerOperationsMock() {
        this(new LogString());
    }


    public JdbcServerOperationsMock(LogString log) {
        this.log = log;
        userIdMock = UserId.createId("login", "pwd");
        try {
            connectionPoolMock = new ConnectionPoolMock(new LogString("pool", log));
        }
        catch (ClassNotFoundException e) {
            throw new InternalError(e.getLocalizedMessage());
        }
    }


    public ConnectionPoolMock getConnectionPoolMock() {
        return connectionPoolMock;
    }


    public void mockCreatePool(UserId mock) {
        this.userIdMock = mock;
    }


    public UserId createPool(String login, String password) throws SQLException {
        log.call("createPool", login, password);
        return userIdMock;
    }


    public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException {
        log.call("createPool", userId.getLogin(), login, password);
        return connectionPoolMock;
    }


    public ConnectionPool getPool(UserId userId) throws JdbcServerException {
        log.call("getPool", userId.getLogin());
        return connectionPoolMock;
    }


    public void destroyPool(UserId userId) throws JdbcServerException {
        log.call("destroyPool", userId.getLogin());
    }
}
