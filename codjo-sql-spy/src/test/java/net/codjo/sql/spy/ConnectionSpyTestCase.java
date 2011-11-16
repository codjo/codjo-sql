package net.codjo.sql.spy;
import java.sql.Connection;
import org.mockito.Mockito;
/**
 *
 */
public class ConnectionSpyTestCase {
    protected Connection connection;
    protected ConnectionSpy connectionSpy;


    protected void setUp() throws Exception {
        connection = Mockito.mock(Connection.class);
        connectionSpy = new ConnectionSpy(connection);
        connectionSpy.setDisplayQuery(true);
    }
}
