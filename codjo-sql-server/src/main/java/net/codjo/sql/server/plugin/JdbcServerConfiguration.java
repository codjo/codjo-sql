package net.codjo.sql.server.plugin;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.database.api.Engine;
import net.codjo.sql.server.ConnectionPoolConfiguration;
import net.codjo.sql.server.JdbcServerException;
import java.util.Properties;
/**
 *
 */
public class JdbcServerConfiguration {
    static final String DRIVER_PARAMETER = "JDBCService.driver";
    public static final String ENGINE_PARAMETER = "JDBCService.engine";
    static final String URL_PARAMETER = "JDBCService.url";
    static final String CATALOG_PARAMETER = "JDBCService.catalog";
    static final String PLATFORM_ID = "platform-id";
    private ConnectionPoolConfiguration configuration = new ConnectionPoolConfiguration();
    private Engine engine = Engine.SYBASE;


    public String getUrl() {
        return configuration.getUrl();
    }


    public void setUrl(String url) {
        configuration.setUrl(url);
    }


    public String getCatalog() {
        return configuration.getCatalog();
    }


    public void setCatalog(String catalog) {
        configuration.setCatalog(catalog);
    }


    public Properties getProperties() {
        return configuration.getProperties();
    }


    public String getClassDriver() {
        return configuration.getClassDriver();
    }


    public void setClassDriver(String classDriver) {
        configuration.setClassDriver(classDriver);
    }


    public void setNumericTruncationWarning(boolean numericTruncationWarning) {
        configuration.setNumericTruncationWarning(numericTruncationWarning);
    }


    public boolean getNumericTruncationWarning() {
        return configuration.getNumericTruncationWarning();
    }


    public long getIdleConnectionTimeout() {
        return configuration.getIdleConnectionTimeout();
    }


    public void setIdleConnectionTimeout(long idleConnectionTimeout) {
        configuration.setIdleConnectionTimeout(idleConnectionTimeout);
    }


    public String getApplicationName() {
        return configuration.getApplicationName();
    }


    public void setApplicationName(String applicationName) {
        configuration.setApplicationName(applicationName);
    }


    JdbcServerConfiguration merge(ContainerConfiguration containerConfiguration) throws JdbcServerException {
        JdbcServerConfiguration mergedConfig = new JdbcServerConfiguration();

        Engine anEngine = selectNotNull(getEngine(),
                                        Engine.toEngine(containerConfiguration.getParameter(ENGINE_PARAMETER)));
        assertParameterNotNull(anEngine, "Manque le motreur SQL (sybase, mysql, etc.)", ENGINE_PARAMETER);
        mergedConfig.setEngine(anEngine);

        String driver = selectNotNull(configuration.getClassDriver(),
                                      containerConfiguration.getParameter(DRIVER_PARAMETER));
        assertParameterNotNull(driver, "Manque le driver SQL", DRIVER_PARAMETER);
        mergedConfig.setClassDriver(driver);

        String url = selectNotNull(configuration.getUrl(),
                                   containerConfiguration.getParameter(URL_PARAMETER));
        assertParameterNotNull(url, "Manque l'URL de la BD", URL_PARAMETER);
        mergedConfig.setUrl(url);

        String catalog = selectNotNull(configuration.getCatalog(),
                                       containerConfiguration.getParameter(CATALOG_PARAMETER));
        assertParameterNotNull(catalog, "Manque le catalogue de la BD", CATALOG_PARAMETER);
        mergedConfig.setCatalog(catalog);

        String applicationName = selectNotNull(configuration.getApplicationName(),
                                               containerConfiguration.getParameter(PLATFORM_ID));
        if (applicationName != null) {
            mergedConfig.setApplicationName(applicationName);
        }

        return mergedConfig;
    }


    ConnectionPoolConfiguration toConnectionPoolConfiguration() {
        return configuration.clone();
    }


    private <T> T selectNotNull(T fromCode, T fromConfigurationFile) {
        return fromConfigurationFile != null ? fromConfigurationFile : fromCode;
    }


    private static void assertParameterNotNull(Object value, String message, String propertyKey)
          throws JdbcServerException {
        if (value == null) {
            throw new JdbcServerException(message + " (property " + propertyKey + ")");
        }
    }


    public Engine getEngine() {
        return engine;
    }


    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
