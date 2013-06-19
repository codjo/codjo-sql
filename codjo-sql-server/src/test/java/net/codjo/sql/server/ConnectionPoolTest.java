/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import junit.framework.TestCase;
import net.codjo.database.common.api.ConnectionMetadata;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.JdbcFixtureAdvanced;
import net.codjo.database.common.api.JdbcFixtureUtil;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.test.common.mock.ConnectionMock;
import net.codjo.test.common.mock.StatementMock;

import static net.codjo.sql.server.ConnectionFactoryConfigurationTest.USER1;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Classe de test de {@link ConnectionPool}.
 */
public class ConnectionPoolTest extends TestCase {
    private static final String TABLE_NAME = "#TU_BOBO";
    private JdbcFixture jdbc;
    private ConnectionPool pool;


    @Override
    protected void setUp() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        pool = createConnectionPool(false, new ConnectionFactoryConfiguration(), USER1);
    }


    @Override
    protected void tearDown() {
        pool.closeAllConnections();
    }


    public void test_constructor() throws Exception {
        pool.shutdown();

        ConnectionPoolConfiguration configuration = createConfiguration(false);
        configuration.setAutomaticClose(false);

        pool = new ConnectionPool(configuration, new ConnectionFactoryConfiguration(), USER1);

        assertSame(configuration, pool.getConfiguration());
    }


    public void test_languageInFrench() throws Exception {
        Connection connection = pool.getConnection();
        Statement statement = connection.createStatement();
        try {
            statement.executeQuery("select from");
            fail();
        }
        catch (SQLException exception) {
            assertEquals("Syntaxe incorrecte près du mot clé 'from'.", exception.getLocalizedMessage());
        }
    }


    public void test_shutdown() throws Exception {
        Connection connection = pool.getConnection();

        pool.shutdown();

        assertTrue(connection.isClosed());
    }


    public void test_shutdown_poolCanNotBeUsed() throws Exception {
        pool.getConnection();
        pool.releaseConnection(pool.getConnection());

        assertEquals(2, pool.getAllConnectionsSize());

        pool.shutdown();

        assertEquals(0, pool.getAllConnectionsSize());

        try {
            pool.getConnection();
            fail();
        }
        catch (SQLException ex) {
            assertEquals("Pool has been shut down !", ex.getMessage());
        }
        try {
            pool.releaseConnection(new ConnectionMock().getStub());
            fail();
        }
        catch (SQLException ex) {
            assertEquals("Pool has been shut down !", ex.getMessage());
        }
        try {
            pool.releaseConnection(null, new StatementMock() {

                @Override
                public void close() throws SQLException {
                    throw new Error();
                }
            }.getStub());
            fail();
        }
        catch (SQLException ex) {
            assertEquals("Pool has been shut down !", ex.getMessage());
        }
    }


    public void test_automaticClose() throws Exception {
        //Test que la connection n'est pas fermée après un release
        Connection firstConnection = pool.getConnection();
        pool.releaseConnection(firstConnection);
        Connection secondConnection = pool.getConnection();
        pool.releaseConnection(secondConnection);
        assertSame(firstConnection, secondConnection);

        pool.setIdleConnectionTimeout(200);
        Thread.sleep(300);

        //Test que la connection est fermée après le delai
        Connection delayConnection = pool.getConnection();
        assertTrue(secondConnection != delayConnection);
        assertTrue(secondConnection.isClosed());
    }


    public void test_automaticClose_disabled() throws Exception {
        pool.closeAllConnections();
        pool = createConnectionPoolWithoutAutomaticClose();

        Connection firstConnection = pool.getConnection();
        pool.releaseConnection(firstConnection);
        Connection secondConnection = pool.getConnection();
        pool.releaseConnection(secondConnection);
        assertSame(firstConnection, secondConnection);

        pool.setIdleConnectionTimeout(200);
        Thread.sleep(300);

        //Test que la connection est fermée après le delai
        Connection delayConnection = pool.getConnection();
        assertSame(secondConnection, delayConnection);
    }


    public void test_automaticClose_timeoutByConnection() throws Exception {
        Connection aliveConnection = pool.getConnection();
        Connection oldConnection = pool.getConnection();
        pool.setIdleConnectionTimeout(200);

        pool.releaseConnection(oldConnection);
        Thread.sleep(100);

        pool.releaseConnection(aliveConnection);
        Thread.sleep(110);

        pool.closeOldConnection();
        assertTrue("old closed", oldConnection.isClosed());
        assertFalse("alive encore active", aliveConnection.isClosed());

        Thread.sleep(100);
        pool.closeOldConnection();

        assertTrue("old free", oldConnection.isClosed());
        assertTrue("alive free", aliveConnection.isClosed());
    }


    public void test_getConnection() throws SQLException {
        Connection connection = pool.getConnection();
        assertTrue(!connection.isClosed());
        pool.releaseConnection(connection);
    }


    public void test_closeAllConnections() throws SQLException {
        Connection connection = pool.getConnection();
        assertTrue("Connection ouverte", !connection.isClosed());
        pool.closeAllConnections();
        assertTrue("Connection ferme", connection.isClosed());
    }


    public void test_insert_truncature() throws Exception {
        Connection connection = pool.getConnection();
        JdbcFixture fixture = getJdbcFixture(connection);

        createTemporaryTable(connection);

        // --- Insert using Statement
        insertUsingStatement(connection, "125.235");

        fixture.assertContent(SqlTable.table(TABLE_NAME), oneRow("125.23"));
        fixture.delete(SqlTable.table(TABLE_NAME));

        // --- Insert using PreparedStatement
        PreparedStatement preparedStatement =
              connection.prepareStatement("insert into " + TABLE_NAME + " values (?)");
        preparedStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        int nbOfInsertedRow = preparedStatement.executeUpdate();
        assertEquals(1, nbOfInsertedRow);
        assertNull("Troncature sans warning ", preparedStatement.getWarnings());
        preparedStatement.close();

        fixture.assertContent(SqlTable.table(TABLE_NAME), oneRow("125.23"));
        fixture.delete(SqlTable.table(TABLE_NAME));

        // --- Insert using CallableStatement
        CallableStatement callableStatement =
              connection.prepareCall("insert into " + TABLE_NAME + " values (?)");
        callableStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        nbOfInsertedRow = callableStatement.executeUpdate();
        assertEquals(1, nbOfInsertedRow);
        assertNull("Troncature sans warning ", callableStatement.getWarnings());
        callableStatement.close();

        fixture.assertContent(SqlTable.table(TABLE_NAME), oneRow("125.23"));
        fixture.delete(SqlTable.table(TABLE_NAME));
    }


    public void test_insert_withNumericTruncationWarning() throws Exception {
        pool.closeAllConnections();
        pool = createConnectionPool(true, new ConnectionFactoryConfiguration(), USER1);

        Connection connection = pool.getConnection();
        JdbcFixture fixture = getJdbcFixture(connection);
        createTemporaryTable(connection);

        // --- Insert using Statement
        try {
            insertUsingStatement(connection, "125.239");
            fail("Insertion refusé suite à la troncature");
        }
        catch (SQLException e) {
            ;
        }

        fixture.assertIsEmpty(SqlTable.table(TABLE_NAME));

        // --- Insert using PreparedStatement
        PreparedStatement preparedStatement =
              connection.prepareStatement("insert into " + TABLE_NAME + " values (?)");
        preparedStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        int nbOfInsertedRow = preparedStatement.executeUpdate();
        assertEquals(0, nbOfInsertedRow);
        assertNotNull("Troncature avec un warning ", preparedStatement.getWarnings());
        preparedStatement.close();

        fixture.assertIsEmpty(SqlTable.table(TABLE_NAME));

        // --- Insert using CallableStatement
        CallableStatement callableStatement =
              connection.prepareCall("insert into " + TABLE_NAME + " values (?)");
        callableStatement.setBigDecimal(1, new java.math.BigDecimal("125.235"));
        nbOfInsertedRow = callableStatement.executeUpdate();
        assertEquals(0, nbOfInsertedRow);
        assertNotNull("Troncature avec un warning ", callableStatement.getWarnings());
        callableStatement.close();

        fixture.assertIsEmpty(SqlTable.table(TABLE_NAME));
    }


    public void test_getConnection_afterBadRelease() throws SQLException {
        Connection connection = pool.getConnection();
        connection.close();
        pool.releaseConnection(connection);

        connection = pool.getConnection();
        assertTrue(!connection.isClosed());
    }


    public void test_closeAllConnections_2pools() throws Exception {
        pool.releaseConnection(pool.getConnection());
        pool.closeAllConnections();

        ConnectionPool otherConnectionPool = createConnectionPool(false, new ConnectionFactoryConfiguration(), USER1);
        otherConnectionPool.releaseConnection(otherConnectionPool.getConnection());
        pool.closeAllConnections();
        otherConnectionPool.closeAllConnections();
    }


    private ConnectionPool createConnectionPool(boolean numericTruncationWarning,
                                                ConnectionFactoryConfiguration cfConfiguration,
                                                String login) {
        return new ConnectionPool(createConfiguration(numericTruncationWarning), cfConfiguration, login);
    }


    private ConnectionPool createConnectionPoolWithoutAutomaticClose() {
        ConnectionPoolConfiguration configuration = createConfiguration(false);
        configuration.setAutomaticClose(false);
        return new ConnectionPool(configuration, new ConnectionFactoryConfiguration(), USER1);
    }


    private ConnectionPoolConfiguration createConfiguration(boolean numericTruncationWarning) {
        ConnectionMetadata metadata = jdbc.advanced().getConnectionMetadata();
        DatabaseHelper helper = new DatabaseFactory().createDatabaseHelper();

        Properties properties = new Properties();
        properties.put("user", metadata.getUser());
        properties.put("password", metadata.getPassword());

        return new ConnectionPoolConfiguration(helper.getDriverClassName(),
                                               helper.getConnectionUrl(metadata),
                                               metadata.getCatalog(),
                                               properties,
                                               numericTruncationWarning);
    }


    public void test_releaseConnection() throws SQLException {
        Connection connection = pool.getConnection();

        connection.setAutoCommit(false);

        pool.releaseConnection(connection);

        assertSame(connection, pool.getConnection());
        assertTrue(connection.getAutoCommit());
        assertNull(connection.getWarnings());
    }


    public void test_releaseConnection_twice() throws SQLException {
        Connection connection = pool.getConnection();

        pool.releaseConnection(connection);
        pool.releaseConnection(connection);

        assertSame(connection, pool.getConnection());
        assertNotSame(connection, pool.getConnection());
    }


    public void test_releaseConnection_notAPoolConnection() throws SQLException {
        try {
            pool.releaseConnection(new ConnectionMock().getStub());
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Cette connexion n'appartient pas à ce pool !", ex.getMessage());
        }
    }


    public void test_releaseConnection_statement() throws SQLException {
        Connection connection = pool.getConnection();

        StatementMock statement = new StatementMock();

        pool.releaseConnection(connection, statement.getStub());

        assertEquals("close()", statement.callList());
        assertSame(connection, pool.getConnection());
    }


    public void test_releaseConnection_statementError() throws SQLException {
        Connection connection = pool.getConnection();

        StatementMock dummyObject = new StatementMock() {
            @Override
            public void close() throws SQLException {
                throw new SQLException("failure during close");
            }
        };

        try {
            pool.releaseConnection(connection, dummyObject.getStub());
            fail();
        }
        catch (SQLException ex) {
            assertEquals("failure during close", ex.getMessage());
        }

        assertSame(connection, pool.getConnection());
    }


    public void test_releaseConnection_null() throws SQLException {
        pool.releaseConnection(null);
    }


    public void test_getConnection_3times() throws SQLException {
        pool.getConnection();
        pool.getConnection();
        pool.getConnection();
    }


    public void test_fillPool() throws SQLException {
        pool.fillPool(1);
        assertEquals(1, pool.getAllConnectionsSize());
    }


    public void test_ConnectionFactory_allUsers() throws SQLException {
        test_UserConnectionFactory(true);
    }


    public void test_ConnectionFactory_user() throws SQLException {
        test_UserConnectionFactory(false);
    }


    private void test_UserConnectionFactory(boolean allUsers) throws SQLException {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
        Connection mockConnection = mock(Connection.class);
        when(mockConnectionFactory.createConnection(any(ConnectionPoolConfiguration.class), eq(USER1))).thenReturn(
              mockConnection);

        ConnectionFactoryConfiguration cfConfiguration = new ConnectionFactoryConfiguration();
        if (allUsers) {
            cfConfiguration.setDefaultConnectionFactory(mockConnectionFactory);
        }
        else {
            cfConfiguration.setConnectionFactory(USER1, mockConnectionFactory);
        }

        Connection connection = test_ConnectionFactory(USER1, cfConfiguration, mockConnectionFactory.getClass());

        verify(mockConnectionFactory, times(1)).createConnection(any(ConnectionPoolConfiguration.class),
                                                                 eq(USER1));
        assertEquals("Connection returned by factory is not used", mockConnection, connection);
    }


    private Connection test_ConnectionFactory(String login,
                                              ConnectionFactoryConfiguration cfConfiguration,
                                              Class<? extends ConnectionFactory> expectedConnectionFactory)
          throws SQLException {
        pool.closeAllConnections();

        ConnectionFactory factory = cfConfiguration.getConnectionFactory(login);
        assertEquals("wrong actual ConnectionFactory class",
                     expectedConnectionFactory,
                     (factory == null) ? null : factory.getClass());

        pool = createConnectionPool(true, cfConfiguration, login);
        Connection connection = pool.getConnection();

        return connection;
    }


    private void createTemporaryTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("create table " + TABLE_NAME + " ( AMOUNT numeric(12,2)   null )");
        statement.close();
    }


    private void insertUsingStatement(Connection connection, String value) throws SQLException {
        Statement statement = connection.createStatement();
        int nbOfInsertedRow =
              statement.executeUpdate("insert into " + TABLE_NAME + " values (" + value + ")");

        assertTrue("Ligne insérée ", nbOfInsertedRow == 1);
        assertTrue("Troncature sans aucun warning (mode par défaut)", statement.getWarnings() == null);
        statement.close();
    }


    private JdbcFixture getJdbcFixture(final Connection connection) throws SQLException {
        return new MyAbstractJdbcFixture(connection);
    }


    private String[][] oneRow(String value) {
        return new String[][]{{value}};
    }


    private static class MyAbstractJdbcFixture extends JdbcFixture {
        private final Connection connection;


        private MyAbstractJdbcFixture(Connection connection) {
            super(null);
            this.connection = connection;
        }


        @Override
        public Connection getConnection() {
            return connection;
        }


        @Override
        protected JdbcFixtureAdvanced newJdbcFixtureAdvanced() {
            return null;
        }


        @Override
        protected JdbcFixtureUtil newJdbcFixtureUtil() {
            return null;
        }
    }
}
