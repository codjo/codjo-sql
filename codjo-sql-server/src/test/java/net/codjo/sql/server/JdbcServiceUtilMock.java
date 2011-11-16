package net.codjo.sql.server;
import net.codjo.agent.AclMessage;
import static net.codjo.agent.AclMessage.performativeToString;
import net.codjo.agent.Agent;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
public class JdbcServiceUtilMock extends JdbcServiceUtil {
    private final LogString log;
    private SQLException getConnectionPoolFailure;
    private JdbcFixture jdbc;


    public JdbcServiceUtilMock(LogString log) {
        this.log = log;
    }


    public JdbcServiceUtilMock(LogString log, JdbcFixture fixture) {
        this.log = log;
        this.jdbc = fixture;
    }


    @Override
    public ConnectionPool getConnectionPool(Agent agent, AclMessage message)
          throws SQLException {
        if (getConnectionPoolFailure != null) {
            throw getConnectionPoolFailure;
        }

        log.call("getConnectionPool",
                 (agent == null ? "null" : agent.getClass().getSimpleName()),
                 "message:" + (message == null ? "null" : performativeToString(message.getPerformative())));
        try {
            ConnectionPoolMock pool = new ConnectionPoolMock();
            if (jdbc != null) {
                pool.mockGetConnection(jdbc.getConnection());
            }
            return pool;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }


    public JdbcServiceUtilMock mockGetConnectionPoolFailure(SQLException error) {
        getConnectionPoolFailure = error;
        return this;
    }
}

