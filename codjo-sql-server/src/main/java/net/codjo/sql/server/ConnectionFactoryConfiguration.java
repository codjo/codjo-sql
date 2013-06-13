package net.codjo.sql.server;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Configuration for the {@link ConnectionFactory}. There are 3 levels of {@link ConnectionFactory} (from higher to
 * lower priority) : <ol> <li>User's level : a factory associated with a given user</li> <li>{@link #ALL_USERS}'s level
 * : a factory associated all users without a specific factory</li> <li>{@link #DEFAULT} level : this one give the
 * ultimate default factory (never null, not modifiable)</li> </ol> Note : The {@link #DEFAULT} factory doesn't spy jdbc
 * connections (but <code>net.codjo.mad.server.Handler</code>s might still be spied ...).
 */
class ConnectionFactoryConfiguration {
    private static final Logger LOG = Logger.getLogger(ConnectionFactoryConfiguration.class);

    private static final ConnectionFactory DEFAULT = DefaultConnectionFactory.INSTANCE;
    private static final String ALL_USERS = "ALL_USERS";

    private final Map<String, ConnectionFactory> factories = new HashMap<String, ConnectionFactory>();


    void clearConnectionFactories() {
        synchronized (factories) {
            factories.clear();
        }
    }


    void setDefaultConnectionFactory(ConnectionFactory factory) {
        setConnectionFactoryImpl(ALL_USERS, factory);
    }


    void setConnectionFactory(String login, ConnectionFactory factory) {
        if (login == null) {
            throw new NullPointerException("login is null");
        }

        if (ALL_USERS.equals(login)) {
            throw new IllegalArgumentException("login '" + ALL_USERS + "' is reserved for internal use");
        }

        setConnectionFactoryImpl(login, factory);
    }


    private void setConnectionFactoryImpl(String login, ConnectionFactory factory) {
        synchronized (factories) {
            if (factory == null) {
                factories.remove(login);
            }
            else {
                factories.put(login, factory);
            }
        }
        log("set to", login, factory);
    }


    ConnectionFactory getConnectionFactory(String login) {
        ConnectionFactory result;
        synchronized (factories) {
            // find factory for the given user
            result = factories.get(login);

            if (result == null) {
                // find the factory for all users
                result = factories.get(ALL_USERS);
            }
        }

        if (result == null) {
            // use ultimate default factory (never null)
            result = DEFAULT;
        }

        log("is", login, result);
        return result;
    }


    private void log(String operation, String login, ConnectionFactory factory) {
        if (LOG.isInfoEnabled()) {
            String name = (factory == null) ? "null" : factory.getClass().getName();
            LOG.info("ConnectionFactory for user '" + login + "' " + operation + " " + name);
        }
    }
}
