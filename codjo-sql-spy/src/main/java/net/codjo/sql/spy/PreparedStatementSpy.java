package net.codjo.sql.spy;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import net.codjo.util.time.SystemTimeSource;
import net.codjo.util.time.TimeSource;
import org.apache.log4j.Logger;

public class PreparedStatementSpy extends PreparedStatementDecorator {
    private static final Logger LOGGER = Logger.getLogger(PreparedStatementSpy.class);
    private final SpyUtil spyUtil = new SpyUtil();
    private ConnectionSpy connectionSpy;
    private String prepareQuery;
    private String[] parameters;
    private final TimeSource timeSource;


    public PreparedStatementSpy(PreparedStatement statement, String query, ConnectionSpy connectionSpy) {
        this(statement, query, connectionSpy, null);
    }


    public PreparedStatementSpy(PreparedStatement statement,
                                String query,
                                ConnectionSpy connectionSpy,
                                TimeSource timeSource) {
        super(statement);
        this.prepareQuery = query;
        this.connectionSpy = connectionSpy;

        int nbParams = spyUtil.countParameters(query);
        if (nbParams > 0) {
            parameters = new String[nbParams];
        }

        this.timeSource = SystemTimeSource.defaultIfNull(timeSource);
    }


    @Override
    public ResultSet executeQuery() throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.prepared.executeQuery(" + builtQuery().replaceAll("\n", " ") + ")");
        }
        long start = timeSource.getTime();
        try {
            return super.executeQuery();
        }
        finally {
            connectionSpy.getPreparedStatement(prepareQuery).addTime(timeSource.getTime() - start);
        }
    }


    @Override
    public int executeUpdate() throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.prepared.executeUpdate(" + builtQuery().replaceAll("\n", " ") + ")");
        }
        long start = timeSource.getTime();
        try {
            return super.executeUpdate();
        }
        finally {
            connectionSpy.getPreparedStatement(prepareQuery).addTime(timeSource.getTime() - start);
        }
    }


    @Override
    public boolean execute() throws SQLException {
        if (connectionSpy.shouldDisplayQuery()) {
            LOGGER.info("$$.prepared.execute(" + builtQuery().replaceAll("\n", " ") + ")");
        }
        long start = timeSource.getTime();
        try {
            return super.execute();
        }
        finally {
            connectionSpy.getPreparedStatement(prepareQuery).addTime(timeSource.getTime() - start);
        }
    }


    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        super.setNull(parameterIndex, sqlType);
        setValue(parameterIndex, sqlType);
    }


    @Override
    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        super.setNull(paramIndex, sqlType, typeName);
        setValue(paramIndex, sqlType);
    }


    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        super.setBoolean(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        super.setByte(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        super.setShort(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        super.setInt(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        super.setLong(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        super.setFloat(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        super.setDouble(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        super.setBigDecimal(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        super.setString(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        super.setBytes(parameterIndex, x);
        setValue(parameterIndex, Arrays.toString(x));
    }


    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        super.setDate(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        super.setDate(parameterIndex, x, cal);
        setValue(parameterIndex, x);
    }


    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        super.setTime(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        super.setTime(parameterIndex, x, cal);
        setValue(parameterIndex, x);
    }


    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        super.setTimestamp(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        super.setTimestamp(parameterIndex, x, cal);
        setValue(parameterIndex, x);
    }


    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        super.setAsciiStream(parameterIndex, x, length);
        setValue(parameterIndex, x);
    }


    @SuppressWarnings({"deprecation"})
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        super.setUnicodeStream(parameterIndex, x, length);
        setValue(parameterIndex, x);
    }


    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        super.setBinaryStream(parameterIndex, x, length);
        setValue(parameterIndex, x);
    }


    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        super.setCharacterStream(parameterIndex, reader, length);
        setValue(parameterIndex, reader);
    }


    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        super.setObject(parameterIndex, x, targetSqlType, scale);
        setValue(parameterIndex, x);
    }


    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        super.setObject(parameterIndex, x, targetSqlType);
        setValue(parameterIndex, x);
    }


    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        super.setObject(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void setRef(int i, Ref x) throws SQLException {
        super.setRef(i, x);
        setValue(i, x);
    }


    @Override
    public void setBlob(int i, Blob x) throws SQLException {
        super.setBlob(i, x);
        setValue(i, x);
    }


    @Override
    public void setClob(int i, Clob x) throws SQLException {
        super.setClob(i, x);
        setValue(i, x);
    }


    @Override
    public void setArray(int i, Array x) throws SQLException {
        super.setArray(i, x);
        setValue(i, x);
    }


    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        super.setURL(parameterIndex, x);
        setValue(parameterIndex, x);
    }


    @Override
    public void clearParameters() throws SQLException {
        super.clearParameters();
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = null;
        }
    }


    private void setValue(int parameterIndex, Object val) {
        spyUtil.setValue(parameters, parameterIndex, val);
    }


    private String builtQuery() throws SQLException {
        return spyUtil.builtQuery(parameters, prepareQuery);
    }
}
