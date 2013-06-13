package net.codjo.sql.server;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Interface for a factory that creates {@link java.sql.Connection}s.
 */
public interface ConnectionFactory {
    Connection createConnection(ConnectionPoolConfiguration configuration, String applicationUser) throws SQLException;
}
