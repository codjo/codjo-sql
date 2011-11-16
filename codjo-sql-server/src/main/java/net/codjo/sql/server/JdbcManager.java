package net.codjo.sql.server;
import net.codjo.agent.UserId;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
/**
 * Service permettant l'accès à une base de données.
 */
public class JdbcManager {
    private final Logger logger = Logger.getLogger(JdbcManager.class.getName());
    private final Object lock = new Object();
    private final Map<UserId, ConnectionPool> poolMap = new HashMap<UserId, ConnectionPool>();
    private final ConnectionPoolConfiguration poolConfigurationPrototype;
    static final int DEFAULT_POOL_SIZE = 1;


    public JdbcManager(ConnectionPoolConfiguration configurationPrototype) {
        this.poolConfigurationPrototype = configurationPrototype;
    }


    public ConnectionPool createPool(UserId userId, String login, String password) throws SQLException {
        synchronized (lock) {
            logger.info("Construction du pool " + userId.getLogin() + "/" + userId.getObjectId());

            ConnectionPoolConfiguration configuration = poolConfigurationPrototype.clone();
            configuration.setUser(login);
            configuration.setPassword(password);
            configuration.setHostname(userId.getLogin());

            ConnectionPool pool = new ConnectionPool(configuration);
            pool.fillPool(DEFAULT_POOL_SIZE);
            poolMap.put(userId, pool);
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
}
