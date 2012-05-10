package net.codjo.sql.server;
import java.util.Properties;
/**
 *
 */
public class ConnectionPoolConfiguration implements Cloneable {
    static final String SQLINITSTRING_KEY = "SQLINITSTRING";
    static final String APPLICATIONNAME_KEY = "APPLICATIONNAME";
    static final String LANGUAGE_KEY = "LANGUAGE";
    static final String USER_KEY = "user";
    static final String PASSWORD_KEY = "password";
    static final String HOSTNAME_KEY = "HOSTNAME";

    static final String SET_TRUNCATION_ON = "set arithabort numeric_truncation on";
    static final String SET_TRUNCATION_OFF = "set arithabort numeric_truncation off";
    static final String FRENCH = "french";
    private String classDriver;
    private String url;
    private String catalog;
    private String language;
    private Properties properties;
    private long idleConnectionTimeout = 15000;
    private boolean automaticClose;
    private static final String DEFAULT_APPLICATION_NAME = "Application Allianz GI France";


    public ConnectionPoolConfiguration() {
        this(null, null, null, new Properties(), false);
    }


    public ConnectionPoolConfiguration(String driver, String url, String catalog, Properties properties) {
        this(driver, url, catalog, properties, false);
    }


    public ConnectionPoolConfiguration(String classDriver,
                                       String url,
                                       String catalog,
                                       Properties properties,
                                       boolean numericTruncationWarning) {
        this.url = url;
        this.catalog = catalog;
        this.properties = properties;
        this.properties.put(LANGUAGE_KEY, FRENCH);
        setClassDriver(classDriver);
        setNumericTruncationWarning(numericTruncationWarning);
        setAutomaticClose(true);
        if (!properties.containsKey(APPLICATIONNAME_KEY)) {
            setApplicationName(DEFAULT_APPLICATION_NAME);
        }
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getCatalog() {
        return catalog;
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
        properties.setProperty(LANGUAGE_KEY, language);
    }


    public Properties getProperties() {
        return properties;
    }


    public String getClassDriver() {
        return classDriver;
    }


    public void setClassDriver(String classDriver) {
        if (classDriver != null) {
            try {
                Class.forName(classDriver);
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Driver JDBC introuvable : " + classDriver, e);
            }
        }
        this.classDriver = classDriver;
    }


    public void setNumericTruncationWarning(boolean numericTruncationWarning) {
        String truncationCommand;
        if (numericTruncationWarning) {
            truncationCommand = SET_TRUNCATION_ON;
        }
        else {
            truncationCommand = SET_TRUNCATION_OFF;
        }

        String previousValue = properties.getProperty(SQLINITSTRING_KEY);
        if (previousValue != null) {
            previousValue =
                  previousValue.replace(SET_TRUNCATION_OFF, "")
                        .replace(SET_TRUNCATION_ON, "")
                        .trim();
        }
        else {
            previousValue = "";
        }
        properties.put(SQLINITSTRING_KEY,
                       truncationCommand + (previousValue.length() != 0 ? " " + previousValue : ""));
    }


    public boolean getNumericTruncationWarning() {
        String initSql = properties.getProperty(SQLINITSTRING_KEY);
        return initSql != null && initSql.contains(SET_TRUNCATION_ON);
    }


    public long getIdleConnectionTimeout() {
        return idleConnectionTimeout;
    }


    public void setIdleConnectionTimeout(long idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }


    public boolean isAutomaticClose() {
        return automaticClose;
    }


    public void setAutomaticClose(boolean automaticClose) {
        this.automaticClose = automaticClose;
    }


    public String getApplicationName() {
        return getProperties().getProperty(APPLICATIONNAME_KEY);
    }


    public void setApplicationName(String applicationName) {
        if (applicationName == null) {
            getProperties().setProperty(APPLICATIONNAME_KEY, DEFAULT_APPLICATION_NAME);
            return;
        }
        if (applicationName.length() > 30) {
            applicationName = applicationName.substring(0, 30);
        }
        getProperties().setProperty(APPLICATIONNAME_KEY, applicationName);
    }


    @Override
    public ConnectionPoolConfiguration clone() {
        try {
            ConnectionPoolConfiguration clone = (ConnectionPoolConfiguration)super.clone();
            clone.properties = (Properties)properties.clone();
            return clone;
        }
        catch (CloneNotSupportedException cause) {
            // Impossible mais il faut quand même faire compiler
            throw new Error("Clone en echec", cause);
        }
    }


    public void setUser(String user) {
        getProperties().put(USER_KEY, user);
    }


    public String getUser() {
        return getProperties().getProperty(USER_KEY);
    }


    public void setPassword(String password) {
        getProperties().put(PASSWORD_KEY, password);
    }


    public String getPassword() {
        return getProperties().getProperty(PASSWORD_KEY);
    }


    public void setHostname(String hostname) {
        getProperties().put(HOSTNAME_KEY, hostname);
    }


    public String getHostname() {
        return getProperties().getProperty(HOSTNAME_KEY);
    }
}
