package net.codjo.sql.spy;
import java.sql.Connection;
import net.codjo.util.time.MockTimeSource;
import net.codjo.util.time.TimeSource;
import org.mockito.Mockito;
/**
 *
 */
abstract public class ConnectionSpyTestCase {
    protected Connection connection;
    protected ConnectionSpy connectionSpy;
    protected TimeSource timeSource;


    protected void setUp() throws Exception {
        timeSource = new MockTimeSource();

        connection = Mockito.mock(Connection.class);
        connectionSpy = new ConnectionSpy(connection, timeSource);
        connectionSpy.setDisplayQuery(true);
    }
}
