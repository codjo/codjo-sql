package net.codjo.sql.server;
import net.codjo.agent.UserId;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class JdbcManagerMock extends JdbcManager {
    private final LogString log;
    private final Map<UserId, ConnectionPoolMock> userIdConnectionPoolMockMap
          = new HashMap<UserId, ConnectionPoolMock>();


    public JdbcManagerMock() {
        this(new LogString());
    }


    public JdbcManagerMock(LogString log) {
        super(new ConnectionPoolConfiguration());
        this.log = log;
    }


    @Override
    public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException {
        log.call("createPool", toString(userId), login, password);
        try {
            ConnectionPoolMock connectionPool = new ConnectionPoolMock(new LogString("pool", log));
            userIdConnectionPoolMockMap.put(userId, connectionPool);
            return connectionPool;
        }
        catch (ClassNotFoundException e) {
            throw new InternalError(e.getLocalizedMessage());
        }
    }


    @Override
    public ConnectionPool getPool(UserId userId) throws JdbcServerException {
        log.call("getPool", toString(userId));
        return userIdConnectionPoolMockMap.get(userId);
    }


    @Override
    public void destroyPool(UserId userId) throws JdbcServerException {
        log.call("destroyPool", toString(userId));
        userIdConnectionPoolMockMap.remove(userId);
    }


    private String toString(UserId userId) {
        return userId == null ? "null" : userId.getLogin();
    }
}
