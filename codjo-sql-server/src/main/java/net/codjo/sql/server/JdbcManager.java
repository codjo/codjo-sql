package net.codjo.sql.server;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.codjo.agent.UserId;
import org.apache.log4j.Logger;
/**
 * Service permettant l'accès à une base de données.
 */
public class JdbcManager {
    private final Logger logger = Logger.getLogger(JdbcManager.class.getName());
    private final Object lock = new Object();
    private final Map<UserId, ConnectionPool> poolMap = new HashMap<UserId, ConnectionPool>();
    private final ConnectionPoolConfiguration poolConfigurationPrototype;
    private final List<ConnectionPoolListener> poolListeners = new ArrayList<ConnectionPoolListener>(2);

    static final int DEFAULT_POOL_SIZE = 1;

    private final ConnectionFactoryConfiguration connectionFactoryConfiguration = new ConnectionFactoryConfiguration();


    public JdbcManager(ConnectionPoolConfiguration configurationPrototype) {
        this.poolConfigurationPrototype = configurationPrototype;
    }


    /**
     * Clear all connection factories.
     */
    public void clearConnectionFactories() {
        connectionFactoryConfiguration.clearConnectionFactories();
    }


    /**
     * Set the default {@link ConnectionFactory} to use in case there is none specified for a given user.
     */
    public void setDefaultConnectionFactory(ConnectionFactory factory) {
        connectionFactoryConfiguration.setDefaultConnectionFactory(factory);
    }


    /**
     * Set the {@link ConnectionFactory} to use for the given user.
     *
     * @param login   The application user (not to be confused with the database user).
     * @param factory The factory to use for the given user, or null to remove any factory that might have been
     *                associated with the user.
     */
    public void setConnectionFactory(String login, ConnectionFactory factory) {
        connectionFactoryConfiguration.setConnectionFactory(login, factory);
    }


    public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException {
        synchronized (lock) {
            logger.info("Construction du pool " + userId.getLogin() + "/" + userId.getObjectId());

            ConnectionPoolConfiguration configuration = poolConfigurationPrototype.clone();
            configuration.setUser(login);
            configuration.setPassword(password);
            configuration.setHostname(userId.getLogin());

            ConnectionPool pool = new ConnectionPool(configuration, connectionFactoryConfiguration, userId.getLogin());
            pool.fillPool(DEFAULT_POOL_SIZE);
            poolMap.put(userId, pool);
            firePoolCreated(pool);
            return pool;
        }
    }


    public ConnectionPool getPool(UserId poolUserId) throws JdbcServerException {
        synchronized (lock) {
            ConnectionPool connectionPool = poolMap.get(poolUserId);
            assertPoolNotNull(connectionPool, poolUserId);
            return connectionPool;
        }
    }


    public void destroyPool(UserId poolUserId) throws JdbcServerException {
        ConnectionPool connectionPool;
        synchronized (lock) {
            connectionPool = poolMap.remove(poolUserId);
        }
        assertPoolNotNull(connectionPool, poolUserId);
        connectionPool.shutdown();
        firePoolDestroyed(connectionPool);
        logger.info("Destruction du pool " + poolUserId.getLogin() + "/" + poolUserId.getObjectId());
    }


    public Set<UserId> getUserIds() {
        synchronized (lock) {
            return new HashSet<UserId>(poolMap.keySet());
        }
    }


    private static void assertPoolNotNull(ConnectionPool connectionPool, UserId poolUserId)
          throws JdbcServerException {
        if (connectionPool == null) {
            throw new JdbcServerException("Le pool associé à cette clef ("
                                          + (poolUserId != null ? poolUserId.encode() : "null")
                                          + ") n'existe pas !");
        }
    }


    public void addConnectionPoolListener(ConnectionPoolListener l) {
        synchronized (poolListeners) {
            if (!poolListeners.contains(l)) {
                poolListeners.add(l);
            }
        }
    }


    public void removeConnectionPoolListener(ConnectionPoolListener l) {
        synchronized (poolListeners) {
            poolListeners.remove(l);
        }
    }


    private void firePoolCreated(ConnectionPool pool) {
        for (ConnectionPoolListener l : cloneListenerList()) {
            l.poolCreated(pool);
        }
    }


    private void firePoolDestroyed(ConnectionPool pool) {
        for (ConnectionPoolListener l : cloneListenerList()) {
            l.poolDestroyed(pool);
        }
    }


    /**
     * Get a copy of {#poolListeners} to avoid concurrency issues and minimize lock on it.
     */
    private List<ConnectionPoolListener> cloneListenerList() {
        List<ConnectionPoolListener> list;
        synchronized (poolListeners) {
            list = new ArrayList<ConnectionPoolListener>(poolListeners);
        }
        return list;
    }
}
