package net.codjo.sql.server;
import java.sql.Connection;
import java.util.Set;
import junit.framework.TestCase;
import net.codjo.agent.UserId;
import net.codjo.database.common.api.ConnectionMetadata;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.JdbcFixture;

import static net.codjo.sql.server.ConnectionPoolConfiguration.APPLICATIONNAME_KEY;
import static net.codjo.sql.server.ConnectionPoolConfiguration.HOSTNAME_KEY;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
public class JdbcManagerTest extends TestCase {
    private static final String STRING_WITH_30_CHARS = "123456789012345678901234567890";
    private static final String DRIVER_SYBASE = "com.sybase.jdbc2.jdbc.SybDriver";
    private static final String DATABASE_URL = "jdbc:sybase:Tds:ad_daf1:34105";
    private static final String DATABASE_CATALOG = "LIB_INT";
    private JdbcFixture jdbc;
    private JdbcManager jdbcManager;


    @Override
    protected void setUp() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        if (jdbcManager != null) {
            for (UserId id : jdbcManager.getUserIds()) {
                try {
                    jdbcManager.destroyPool(id);
                }
                catch (JdbcServerException e) {
                    ;
                }
            }
        }
        jdbc.doTearDown();
    }


    public void test_userIdUnicity() {
        UserId user1 = UserId.createId("toto", "tutu");
        UserId user2 = UserId.createId("toto", "tutu");
        assertFalse(user1.equals(user2));
    }


    public void test_createPools_twiceSameUser() throws Exception {
        createJdbcManager();

        UserId key1 = UserId.createId("luke", "padawan");
        UserId key2 = UserId.createId("dark-vador", "lePapa");

        jdbcManager.createPool(key1, getJdbcUser(), getJdbcPassword());
        jdbcManager.createPool(key2, getJdbcUser(), getJdbcPassword());

        ConnectionPool pool1 = jdbcManager.getPool(key1);
        ConnectionPool pool2 = jdbcManager.getPool(key2);

        assertNotSame(pool1, pool2);

        jdbcManager.destroyPool(key1);
        assertTrue(pool1.hasBeenShutdown());
        assertFalse(pool2.hasBeenShutdown());

        jdbcManager.destroyPool(key2);
        assertTrue(pool2.hasBeenShutdown());
    }


    public void test_createPool_alreadyConnected() throws Exception {
        createJdbcManager();

        UserId userId = UserId.createId("user", "secret");

        ConnectionPool pool = jdbcManager.createPool(userId, getJdbcUser(), getJdbcPassword());

        assertEquals(JdbcManager.DEFAULT_POOL_SIZE, pool.getAllConnectionsSize());

        Connection connection = pool.getConnection();
        assertNotNull(connection);
        pool.releaseConnection(connection);
    }


    public void test_createPool() throws Exception {
        createJdbcManager(STRING_WITH_30_CHARS + "coupé");
        UserId userId = UserId.createId("userDicId", "pwd");

        ConnectionPool pool = jdbcManager.createPool(userId, getJdbcUser(), getJdbcPassword());
        assertNotNull(pool);

        ConnectionPoolConfiguration configuration = pool.getConfiguration();
        assertEquals(getDriverClassName(), configuration.getClassDriver());
        assertEquals(getConnectionUrl(), configuration.getUrl());
        assertEquals(getCatalog(), configuration.getCatalog());
        assertEquals(getJdbcUser(), configuration.getProperties().get(DatabaseHelper.USER_KEY));
        assertEquals(getJdbcPassword(), configuration.getProperties().get(DatabaseHelper.PASSWORD_KEY));
        assertEquals("userDicId", configuration.getProperties().get(HOSTNAME_KEY));
        assertEquals(STRING_WITH_30_CHARS, configuration.getProperties().getProperty(APPLICATIONNAME_KEY));

        assertSame(pool, jdbcManager.getPool(userId));
        assertSame(pool, jdbcManager.getPool(userId));
    }


    public void test_getPool() throws Exception {
        createJdbcManager();
        UserId userId = UserId.createId("login", "pwd");
        jdbcManager.createPool(userId, getJdbcUser(), getJdbcPassword());
        ConnectionPool pool = jdbcManager.getPool(userId);
        assertNotNull(pool);

        ConnectionPoolConfiguration configuration = pool.getConfiguration();
        assertEquals(getDriverClassName(), configuration.getClassDriver());
        assertEquals(getConnectionUrl(), configuration.getUrl());
        assertEquals(getCatalog(), configuration.getCatalog());
        assertEquals(getJdbcUser(), configuration.getProperties().get(DatabaseHelper.USER_KEY));

        assertSame(pool, jdbcManager.getPool(userId));
    }


    public void test_getPool_badKey() throws Exception {
        createJdbcManager(DRIVER_SYBASE, DATABASE_URL, DATABASE_CATALOG);
        try {
            jdbcManager.getPool(null);
            fail();
        }
        catch (JdbcServerException ex) {
            assertEquals("Le pool associé à cette clef (null) n'existe pas !", ex.getMessage());
        }

        UserId unknownUserId = UserId.createId("badLog", "badPwd");
        try {
            jdbcManager.getPool(unknownUserId);
            fail();
        }
        catch (JdbcServerException ex) {
            assertEquals("Le pool associé à cette clef (" + unknownUserId.encode() + ") n'existe pas !",
                         ex.getMessage());
        }
    }


    public void test_destroyPool() throws Exception {
        createJdbcManager();

        UserId userId = UserId.createId("login", "pwd");
        jdbcManager.createPool(userId, getJdbcUser(), getJdbcPassword());

        ConnectionPool pool = jdbcManager.getPool(userId);

        jdbcManager.destroyPool(userId);

        assertTrue(pool.hasBeenShutdown());

        try {
            jdbcManager.getPool(userId);
            fail();
        }
        catch (JdbcServerException ex) {
        }
    }


    public void test_destroyPool_badKey() throws Exception {
        createJdbcManager();

        UserId badId = UserId.createId("bad", "bad");
        try {
            jdbcManager.destroyPool(badId);
            fail();
        }
        catch (JdbcServerException ex) {
            assertEquals("Le pool associé à cette clef (" + badId.encode() + ") n'existe pas !",
                         ex.getMessage());
        }
    }


    public void test_getUserIds() throws Exception {
        createJdbcManager();

        assertEquals(0, jdbcManager.getUserIds().size());

        UserId otherUserId = UserId.createId("luke", "padawan");
        jdbcManager.createPool(otherUserId, getJdbcUser(), getJdbcPassword());

        Set<UserId> userIds = jdbcManager.getUserIds();
        assertEquals(1, userIds.size());
        assertTrue(userIds.contains(otherUserId));

        assertNotSame(jdbcManager.getUserIds(), jdbcManager.getUserIds());
    }


    public void test_defaultConnectionFactory() throws Exception {
        createJdbcManager();

        assertEquals(0, jdbcManager.getUserIds().size());
    }


    public void testAddConnectionPoolListener_singleListener() throws Exception {
        doTestAddConnectionPoolListener(null);
    }


    public void testAddConnectionPoolListener_duplicateListener() throws Exception {
        ConnectionPoolListener listener = doTestAddConnectionPoolListener(null).listener;

        // add the listener again
        doTestAddConnectionPoolListener(listener);
    }


    public void testRemoveConnectionPoolListener() throws Exception {
        // preparation
        ListenerTestResult addResult = doTestAddConnectionPoolListener(null);

        // test
        jdbcManager.removeConnectionPoolListener(addResult.listener);
        reset(addResult.listener); // clear call history for mock
        ConnectionPool pool = createAndDestroyPool(null);

        // verifications
        verify(addResult.listener, never()).poolCreated(eq(pool));
        verify(addResult.listener, never()).poolDestroyed(eq(pool));
    }


    private ListenerTestResult doTestAddConnectionPoolListener(ConnectionPoolListener listener) throws Exception {
        if (listener == null) {
            listener = mock(ConnectionPoolListener.class);
        }

        ConnectionPool pool = createAndDestroyPool(listener);

        verify(listener, times(1)).poolCreated(eq(pool));
        verify(listener, times(1)).poolDestroyed(eq(pool));

        return new ListenerTestResult(pool, listener);
    }


    private ConnectionPool createAndDestroyPool(ConnectionPoolListener listener) throws Exception {
        if (jdbcManager == null) {
            createJdbcManager();
        }

        if (listener != null) {
            jdbcManager.addConnectionPoolListener(listener);
        }

        UserId userId = UserId.createId("user", "secret");

        ConnectionPool pool;
        try {
            pool = jdbcManager.createPool(userId, getJdbcUser(), getJdbcPassword());
        }
        finally {
            jdbcManager.destroyPool(userId);
        }
        return pool;
    }


    private static class ListenerTestResult {
        private final ConnectionPool pool;
        private final ConnectionPoolListener listener;


        public ListenerTestResult(ConnectionPool pool, ConnectionPoolListener listener) {
            this.pool = pool;
            this.listener = listener;
        }
    }


    private void createJdbcManager(String driver, String url, String catalog) throws Exception {
        ConnectionPoolConfiguration configuration = new ConnectionPoolConfiguration();
        configuration.setClassDriver(driver);
        configuration.setUrl(url);
        configuration.setCatalog(catalog);
        jdbcManager = new JdbcManager(configuration);
    }


    private void createJdbcManager(String applicationName) throws Exception {
        DatabaseHelper helper = new DatabaseFactory().createDatabaseHelper();
        ConnectionMetadata metadata = jdbc.advanced().getConnectionMetadata();

        ConnectionPoolConfiguration configuration = new ConnectionPoolConfiguration();
        configuration.setClassDriver(helper.getDriverClassName());
        configuration.setUrl(helper.getConnectionUrl(metadata));
        configuration.setCatalog(metadata.getCatalog());
        configuration.setApplicationName(applicationName);
        jdbcManager = new JdbcManager(configuration);
    }


    private void createJdbcManager() throws Exception {
        createJdbcManager("unused");
    }


    private String getJdbcPassword() {
        return jdbc.advanced().getConnectionMetadata().getPassword();
    }


    private String getJdbcUser() {
        return jdbc.advanced().getConnectionMetadata().getUser();
    }


    private String getConnectionUrl() {
        return new DatabaseFactory()
              .createDatabaseHelper()
              .getConnectionUrl(jdbc.advanced().getConnectionMetadata());
    }


    private String getDriverClassName() {
        return new DatabaseFactory().createDatabaseHelper().getDriverClassName();
    }


    private String getCatalog() {
        return jdbc.advanced().getConnectionMetadata().getCatalog();
    }
}
