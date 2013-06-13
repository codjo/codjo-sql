package net.codjo.sql.spy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.codjo.util.time.SystemTimeSource;
import net.codjo.util.time.TimeSource;
import org.apache.log4j.Logger;

public class StatementSpy extends StatementDecorator {
    private static final Logger LOGGER = Logger.getLogger(StatementSpy.class);
    private ConnectionSpy connectionSpy;
    private final TimeSource timeSource;


    public StatementSpy(Statement statement) {
        this(statement, null);
    }


    public StatementSpy(Statement statement, ConnectionSpy connectionSpy) {
        super(statement);
        this.connectionSpy = connectionSpy;
        this.timeSource = SystemTimeSource.defaultIfNull(connectionSpy.timeSource);
    }


    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.statement.executeQuery(" + sql.replaceAll("\n", " ") + ")");
        }
        connectionSpy.addStatement(sql);
        long start = timeSource.getTime();
        try {
            return super.executeQuery(sql);
        }
        finally {
            connectionSpy.getStatement(sql).addTime(timeSource.getTime() - start);
        }
    }


    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.statement.executeUpdate(" + sql.replaceAll("\n", " ") + ")");
        }
        connectionSpy.addStatement(sql);
        long start = timeSource.getTime();
        try {
            return super.executeUpdate(sql);
        }
        finally {
            connectionSpy.getStatement(sql).addTime(timeSource.getTime() - start);
        }
    }


    @Override
    public boolean execute(String sql) throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.statement.execute(" + sql.replaceAll("\n", " ") + ")");
        }
        connectionSpy.addStatement(sql);
        long start = timeSource.getTime();
        try {
            return super.execute(sql);
        }
        finally {
            connectionSpy.getStatement(sql).addTime(timeSource.getTime() - start);
        }
    }
}
