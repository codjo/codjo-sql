package net.codjo.sql.server;
import net.codjo.agent.ServiceException;
import net.codjo.agent.UserId;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
public class JdbcServiceHelperMock extends JdbcServiceHelper {
    private ConnectionPool lastConnectionPool;
    private final LogString logString;
    private SQLException createPoolFailure;


    public JdbcServiceHelperMock() {
        this(new LogString());
    }


    public JdbcServiceHelperMock(LogString logString) {
        super(new JdbcManagerMock());
        this.logString = logString;
    }


    @Override
    public ConnectionPool createPool(UserId userId, String login, String password)
          throws ServiceException, SQLException {
        getLog().call("createPool", "userId{" + userId.getLogin() + "}", login, password);
        if (createPoolFailure != null) {
            throw createPoolFailure;
        }
        if (lastConnectionPool == null) {
            try {
                lastConnectionPool = new ConnectionPoolMock(new LogString("pool", getLog()));
            }
            catch (ClassNotFoundException e) {
                throw new ServiceException(e);
            }
        }
        return lastConnectionPool;
    }


    @Override
    public ConnectionPool getPool(UserId userId) throws ServiceException {
        logString.call("getPool", userId.encode());
        return lastConnectionPool;
    }


    @Override
    public void destroyPool(UserId userId) {
        getLog().call("destroyPool", userId.encode());
    }


    public LogString getLog() {
        return logString;
    }


    public ConnectionPool getLastConnectionPool() {
        return lastConnectionPool;
    }


    public void mockGetPool(ConnectionPool connectionPool) {
        lastConnectionPool = connectionPool;
    }


    public void mockCreatePoolFailure(SQLException failure) {
        createPoolFailure = failure;
    }
}
