/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Classe de mock de {@link ConnectionPool}.
 */
public class ConnectionPoolMock extends ConnectionPool {
    private final LogString logString;
    private Connection lastConnection;
    private Connection getConnection;
    private boolean logClassName = false;


    public ConnectionPoolMock(LogString logString) throws ClassNotFoundException {
        super(new ConnectionPoolConfiguration());
        this.logString = logString;
    }


    public ConnectionPoolMock() throws ClassNotFoundException {
        this(new LogString());
    }


    @Override
    public synchronized Connection getConnection() {
        logString.call("getConnection");
        if (getConnection == null) {
            lastConnection = new ConnectionMock(new LogString("connection", logString));
        }
        else {
            lastConnection = getConnection;
        }
        return lastConnection;
    }


    @Override
    public synchronized void releaseConnection(Connection connection) {
        logString.call("releaseConnection", toString(connection));
    }


    @Override
    public synchronized void releaseConnection(Connection connection, Statement statement)
          throws SQLException {
        logString.call("releaseConnection", toString(connection), toString(statement));
    }


    public Connection getLastConnection() {
        return lastConnection;
    }


    public ConnectionPoolMock mockGetConnection(Connection connection) {
        getConnection = connection;
        return this;
    }


    public ConnectionPoolMock doLogClassName() {
        logClassName = true;
        return this;
    }


    private String toString(Object connection) {
        if (logClassName) {
            return connection.getClass().getSimpleName();
        }
        else {
            return connection.toString();
        }
    }
}
