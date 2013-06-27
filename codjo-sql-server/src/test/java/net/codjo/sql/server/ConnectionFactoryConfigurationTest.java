package net.codjo.sql.server;
import java.sql.Connection;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
/**
 *
 */
@RunWith(Theories.class)
public class ConnectionFactoryConfigurationTest extends TestCase {
    private static final ConfigurationFactory ULTIMATE_DEFAULT = new UltimateDefaultConfigurationFactory();
    private static final ConfigurationFactory DEFAULT = new DefaultConfigurationFactory();
    private static final ConfigurationFactory USER_SPECIFIC = new UserSpecificConfigurationFactory();

    static final String USER1 = "user1";
    static final String USER2 = "user2";

    @DataPoint
    public static final Data ULTIMATE_DEFAULT_USER1 = new Data(ULTIMATE_DEFAULT, USER1, DefaultConnectionFactory.class);
    @DataPoint
    public static final Data ULTIMATE_DEFAULT_USER2 = new Data(ULTIMATE_DEFAULT, USER2, DefaultConnectionFactory.class);
    @DataPoint
    public static final Data DEFAULT_USER1 = new Data(DEFAULT, USER1, CustomDefaultConnectionFactory.class);
    @DataPoint
    public static final Data DEFAULT_USER2 = new Data(DEFAULT, USER2, CustomDefaultConnectionFactory.class);
    @DataPoint
    public static final Data SPECIFIC_USER1 = new Data(USER_SPECIFIC, USER1, User1ConnectionFactory.class);
    @DataPoint
    public static final Data SPECIFIC_USER2 = new Data(USER_SPECIFIC, USER2, User2ConnectionFactory.class);


    @Theory
    public void testGetConnectionFactory(Data data) {
        ConnectionFactoryConfiguration config = data.configFactory.create();

        ConnectionFactory factory = config.getConnectionFactory(data.user);

        assertNotNull(factory);
        assertEquals(data.expectedFactoryClass, factory.getClass());
    }


    @Theory
    public void testClearFactories(Data data) {
        // preparation
        ConnectionFactoryConfiguration config = data.configFactory.create();

        // test
        config.clearConnectionFactories();

        // check
        ConnectionFactory factory = config.getConnectionFactory(data.user);
        assertNotNull(factory);
        assertEquals(DefaultConnectionFactory.class, factory.getClass());
    }


    private static class User1ConnectionFactory extends DefaultConnectionFactory {
        public Connection createConnection(ConnectionPoolConfiguration configuration) throws SQLException {
            return null;
        }
    }

    private static class User2ConnectionFactory extends DefaultConnectionFactory {
        public Connection createConnection(ConnectionPoolConfiguration configuration) throws SQLException {
            return null;
        }
    }

    private static class CustomDefaultConnectionFactory extends DefaultConnectionFactory {
        public Connection createConnection(ConnectionPoolConfiguration configuration) throws SQLException {
            return null;
        }
    }

    private static class Data implements Cloneable {
        private final ConfigurationFactory configFactory;
        private final String user;
        private final Class<? extends ConnectionFactory> expectedFactoryClass;


        public Data(ConfigurationFactory configFactory,
                    String user, Class<? extends ConnectionFactory> factoryClass) {
            this.configFactory = configFactory;
            this.user = user;
            expectedFactoryClass = factoryClass;
        }
    }

    private static interface ConfigurationFactory {
        ConnectionFactoryConfiguration create();
    }

    private static class UltimateDefaultConfigurationFactory implements ConfigurationFactory {
        public ConnectionFactoryConfiguration create() {
            return new ConnectionFactoryConfiguration();
        }
    }

    private static class DefaultConfigurationFactory implements ConfigurationFactory {
        public ConnectionFactoryConfiguration create() {
            ConnectionFactoryConfiguration config = new ConnectionFactoryConfiguration();

            CustomDefaultConnectionFactory customDefault = new CustomDefaultConnectionFactory();
            config.setDefaultConnectionFactory(customDefault);

            return config;
        }
    }

    private static class UserSpecificConfigurationFactory extends DefaultConfigurationFactory {
        public ConnectionFactoryConfiguration create() {
            ConnectionFactoryConfiguration config = super.create();

            config.setConnectionFactory(USER1, new User1ConnectionFactory());
            config.setConnectionFactory(USER2, new User2ConnectionFactory());

            return config;
        }
    }
}
