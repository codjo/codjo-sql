package net.codjo.sql.server;
import java.util.Properties;
import junit.framework.TestCase;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.JdbcFixture;

import static net.codjo.database.common.api.DatabaseHelper.LANGUAGE_KEY;
import static net.codjo.sql.server.ConnectionPoolConfiguration.APPLICATIONNAME_KEY;
import static net.codjo.sql.server.ConnectionPoolConfiguration.HOSTNAME_KEY;
import static net.codjo.sql.server.ConnectionPoolConfiguration.SET_TRUNCATION_OFF;
import static net.codjo.sql.server.ConnectionPoolConfiguration.SET_TRUNCATION_ON;
import static net.codjo.sql.server.ConnectionPoolConfiguration.SQLINITSTRING_KEY;
public class ConnectionPoolConfigurationTest extends TestCase {
    private static final String STRING_WITH_30_CHARS = "123456789012345678901234567890";
    private JdbcFixture jdbc;
    private ConnectionPoolConfiguration configuration = new ConnectionPoolConfiguration();


    @Override
    protected void setUp() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    public void test_setNumericTruncationWarning() throws Exception {
        configuration.setNumericTruncationWarning(true);
        assertTrue(configuration.getNumericTruncationWarning());
        assertEquals(SET_TRUNCATION_ON, configuration.getProperties().get(SQLINITSTRING_KEY));

        configuration.setNumericTruncationWarning(false);
        assertFalse(configuration.getNumericTruncationWarning());
        assertEquals(SET_TRUNCATION_OFF, configuration.getProperties().get(SQLINITSTRING_KEY));
    }


    public void test_setNumericTruncationWarningWithPreviousValue() throws Exception {
        configuration.getProperties().put(SQLINITSTRING_KEY, "sqlcomamnd");
        assertFalse(configuration.getNumericTruncationWarning());

        configuration.setNumericTruncationWarning(true);
        assertTrue(configuration.getNumericTruncationWarning());
        assertEquals(SET_TRUNCATION_ON + " sqlcomamnd", configuration.getProperties().get(SQLINITSTRING_KEY));

        configuration.setNumericTruncationWarning(false);
        assertFalse(configuration.getNumericTruncationWarning());
        assertEquals(SET_TRUNCATION_OFF + " sqlcomamnd",
                     configuration.getProperties().get(SQLINITSTRING_KEY));
    }


    public void test_setClassDriver() throws Exception {
        assertNull(configuration.getClassDriver());
        configuration.setClassDriver(new DatabaseFactory().createDatabaseHelper().getDriverClassName());
        assertEquals(new DatabaseFactory().createDatabaseHelper().getDriverClassName(),
                     configuration.getClassDriver());
    }


    public void test_setClassDriver_badDriver() throws Exception {
        configuration.setClassDriver(new DatabaseFactory().createDatabaseHelper().getDriverClassName());
        try {
            configuration.setClassDriver("uu");
            fail();
        }
        catch (IllegalArgumentException ex) {
            ; // Ok
        }
        assertEquals(new DatabaseFactory().createDatabaseHelper().getDriverClassName(),
                     configuration.getClassDriver());
    }


    public void test_language() throws Exception {
        assertNull(configuration.getLanguage());
        configuration.setLanguage("ENGLISH");
        assertEquals("ENGLISH", configuration.getLanguage());
        assertEquals("ENGLISH", configuration.getProperties().getProperty(LANGUAGE_KEY));
    }


    public void test_automaticCloseDelay() throws Exception {
        assertEquals(15000, configuration.getIdleConnectionTimeout());
        assertTrue(configuration.isAutomaticClose());
        configuration.setAutomaticClose(false);
        assertFalse(configuration.isAutomaticClose());
    }


    public void test_clone() throws Exception {
        configuration.setCatalog("catalog");
        configuration.setAutomaticClose(false);
        configuration.getProperties().put("key", new Object());

        ConnectionPoolConfiguration clone = configuration.clone();

        assertEquals("catalog", clone.getCatalog());
        assertFalse(clone.isAutomaticClose());
        assertNotSame(configuration.getProperties(), clone.getProperties());
        assertSame(configuration.getProperties().get("key"), clone.getProperties().get("key"));
    }


    public void test_applicationName() throws Exception {
        assertEquals("Application Allianz GI France", configuration.getApplicationName());
        assertEquals("Application Allianz GI France",
                     configuration.getProperties().getProperty(APPLICATIONNAME_KEY));

        configuration.setApplicationName("toto");
        assertEquals("toto", configuration.getApplicationName());
    }


    public void test_applicationName_provided() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(APPLICATIONNAME_KEY, "provided");
        configuration = new ConnectionPoolConfiguration(
              new DatabaseFactory().createDatabaseHelper().getDriverClassName(), null, null, properties,
              false);
        assertEquals("provided", configuration.getApplicationName());
    }


    public void test_applicationName_limitedSize() throws Exception {
        configuration.setApplicationName(STRING_WITH_30_CHARS + "coupé");
        assertEquals(STRING_WITH_30_CHARS, configuration.getApplicationName());
    }


    public void test_setUser() throws Exception {
        configuration.setUser("me");
        assertEquals("me", configuration.getUser());
        assertEquals("me", configuration.getProperties().getProperty(DatabaseHelper.USER_KEY));
    }


    public void test_setPassword() throws Exception {
        configuration.setPassword("secret");
        assertEquals("secret", configuration.getPassword());
        assertEquals("secret", configuration.getProperties().getProperty(DatabaseHelper.PASSWORD_KEY));
    }


    public void test_setHostname() throws Exception {
        configuration.setHostname("my true id");
        assertEquals("my true id", configuration.getHostname());
        assertEquals("my true id", configuration.getProperties().getProperty(HOSTNAME_KEY));
    }
}
