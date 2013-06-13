package net.codjo.sql.server;
import java.sql.Connection;
import java.sql.SQLException;
import net.codjo.database.common.api.DatabaseFactory;

/**
 * Default implementation of {@link ConnectionFactory} that is used when no spying of connections is needed.
 */
public class DefaultConnectionFactory implements ConnectionFactory {
    static final ConnectionFactory INSTANCE = new DefaultConnectionFactory();


    protected DefaultConnectionFactory() {
    }


    public Connection createConnection(ConnectionPoolConfiguration configuration, String applicationUser)
          throws SQLException {
        return new DatabaseFactory().createDatabaseHelper().createConnection(configuration.getUrl(),
                                                                             configuration.getProperties());
    }
}
