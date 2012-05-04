package net.codjo.sql.server.plugin;
import junit.framework.TestCase;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.database.api.Engine;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.sql.server.ConnectionPoolConfiguration;
import net.codjo.sql.server.JdbcServerException;
public class JdbcServerConfigurationTest extends TestCase {
    private JdbcServerConfiguration configuration = new JdbcServerConfiguration();
    private ContainerConfiguration containerConfiguration = new ContainerConfiguration();
    private JdbcFixture jdbc;
    private static final String NEED_DRIVER = "Manque le driver SQL (property JDBCService.driver)";
    private static final String NEED_URL = "Manque l'URL de la BD (property JDBCService.url)";
    private static final String NEED_CATALOG = "Manque le catalogue de la BD (property JDBCService.catalog)";
    private DatabaseHelper databaseHelper = new DatabaseFactory().createDatabaseHelper();
    private String catalog;
    private String url;
    private String driver;


    @Override
    protected void setUp() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
        catalog = jdbc.advanced().getConnectionMetadata().getCatalog();
        url = databaseHelper.getConnectionUrl(jdbc.advanced().getConnectionMetadata());
        driver = databaseHelper.getDriverClassName();
    }


    @Override
    protected void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    public void test_jdbcDriver() throws Exception {
        assertEquals(Engine.SYBASE, configuration.getEngine());
    }


    public void test_merge() throws Exception {
        fillJdbcServerConfiguration(driver, url, catalog, "application", "ENGLISH");
        assertMergeResult(driver, url, catalog, "application", "ENGLISH");

        containerConfiguration.setParameter(JdbcServerConfiguration.DRIVER_PARAMETER, "fakedb.FakeDriver");
        assertMergeResult("fakedb.FakeDriver", url, catalog, "application", "ENGLISH");

        containerConfiguration.setParameter(JdbcServerConfiguration.URL_PARAMETER, "overrided-url");
        assertMergeResult("fakedb.FakeDriver", "overrided-url", catalog, "application", "ENGLISH");

        containerConfiguration.setParameter(JdbcServerConfiguration.CATALOG_PARAMETER, "overrided-catalog");
        assertMergeResult("fakedb.FakeDriver", "overrided-url", "overrided-catalog", "application", "ENGLISH");

        containerConfiguration.setParameter(JdbcServerConfiguration.PLATFORM_ID, "overrided-name");
        assertMergeResult("fakedb.FakeDriver", "overrided-url", "overrided-catalog", "overrided-name", "ENGLISH");

        containerConfiguration.setParameter(JdbcServerConfiguration.LANGUAGE_PARAMETER, "overrided-lang");
        assertMergeResult("fakedb.FakeDriver", "overrided-url", "overrided-catalog", "overrided-name", "overrided-lang");
    }


    public void test_toConnectionPoolConfiguration_clone() throws Exception {
        fillJdbcServerConfiguration(driver, url, catalog, "application", "ENGLISH");

        ConnectionPoolConfiguration first = this.configuration.toConnectionPoolConfiguration();
        ConnectionPoolConfiguration second = this.configuration.toConnectionPoolConfiguration();
        assertNotSame(first, second);
    }


    public void test_merge_onlyFromCode() throws Exception {
        assertMergeFailure(NEED_DRIVER);

        configuration.setClassDriver(driver);

        assertMergeFailure(NEED_URL);

        configuration.setUrl(url);

        assertMergeFailure(NEED_CATALOG);

        configuration.setCatalog(catalog);

        configuration.merge(containerConfiguration);
    }


    public void test_merge_onlyFromContainerConfigurationFile() throws Exception {
        assertMergeFailure(NEED_DRIVER);

        containerConfiguration.setParameter(JdbcServerConfiguration.DRIVER_PARAMETER, driver);

        assertMergeFailure(NEED_URL);

        containerConfiguration.setParameter(JdbcServerConfiguration.URL_PARAMETER, url);

        assertMergeFailure(NEED_CATALOG);

        containerConfiguration.setParameter(JdbcServerConfiguration.CATALOG_PARAMETER, catalog);

        configuration.merge(containerConfiguration);
    }


    private void assertMergeFailure(String errorMessage) {
        try {
            configuration.merge(containerConfiguration);
            fail();
        }
        catch (JdbcServerException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }


    private void fillJdbcServerConfiguration(String aDriver,
                                             String anUrl,
                                             String aCatalog,
                                             String applicationName,
                                             String language) {
        configuration.setClassDriver(aDriver);
        configuration.setUrl(anUrl);
        configuration.setCatalog(aCatalog);
        configuration.setApplicationName(applicationName);
        configuration.setLanguage(language);
    }


    private void assertMergeResult(String aDriver, String anUrl, String aCatalog, String applicationName, String language)
          throws JdbcServerException {
        String initialDriver = configuration.getClassDriver();
        String initialUrl = configuration.getUrl();
        String initialCatalog = configuration.getCatalog();
        String initialApplicationName = configuration.getApplicationName();
        String initialLanguage = configuration.getLanguage();

        JdbcServerConfiguration mergedConfig = configuration.merge(containerConfiguration);

        assertConfig(mergedConfig, aDriver, anUrl, aCatalog, applicationName,language);
        assertConfig(configuration, initialDriver, initialUrl, initialCatalog, initialApplicationName, initialLanguage);

        ConnectionPoolConfiguration poolConfiguration = mergedConfig.toConnectionPoolConfiguration();

        assertEquals(aDriver, poolConfiguration.getClassDriver());
        assertEquals(anUrl, poolConfiguration.getUrl());
        assertEquals(aCatalog, poolConfiguration.getCatalog());
        assertEquals(applicationName, poolConfiguration.getApplicationName());
        assertEquals(language, poolConfiguration.getLanguage());
    }


    private void assertConfig(JdbcServerConfiguration mergedConfig, String aDriver,
                              String anUrl,
                              String aCatalog,
                              String applicationName,
                              String language) {
        assertEquals(aDriver, mergedConfig.getClassDriver());
        assertEquals(anUrl, mergedConfig.getUrl());
        assertEquals(aCatalog, mergedConfig.getCatalog());
        assertEquals(applicationName, mergedConfig.getApplicationName());
        assertEquals(language, mergedConfig.getLanguage());
    }
}
