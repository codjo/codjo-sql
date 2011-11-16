package net.codjo.sql.server.util;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;

public class SqlTransactionalExecutor {
    private static final Logger LOG = Logger.getLogger(SqlTransactionalExecutor.class);
    private final List<SqlPrepareImpl> sqlPrepares = new ArrayList<SqlPrepareImpl>();
    private Connection connection;


    private SqlTransactionalExecutor(Connection connection) throws SQLException {
        this.connection = connection;
    }


    public static SqlTransactionalExecutor init(Connection connection) throws SQLException {
        return new SqlTransactionalExecutor(connection);
    }


    public SqlPrepare prepare(String sql) throws SQLException {
        SqlTransactionalExecutor.SqlPrepareImpl sqlPrepare = new SqlPrepareImpl(sql);
        sqlPrepares.add(sqlPrepare);
        return sqlPrepare;
    }


    public void execute() throws SQLException {
        boolean oldAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            for (SqlPrepareImpl sqlPrepare : sqlPrepares) {
                sqlPrepare.execute();
            }
            commit();
        }
        catch (SQLException e) {
            try {
                rollback();
            }
            catch (SQLException e2) {
                ;
            }
            throw e;
        }
        finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }


    private void commit() throws SQLException {
        try {
            connection.commit();
        }
        catch (SQLException e) {
            LOG.error("Erreur lors du commit", e);
            throw e;
        }
    }


    private void rollback() throws SQLException {
        try {
            connection.rollback();
        }
        catch (SQLException e) {
            LOG.error("Erreur lors du rollback", e);
            throw e;
        }
    }


    class SqlPrepareImpl implements SqlPrepare {
        private String sql;
        private PreparedStatement statement;
        int nextIndex = 1;


        SqlPrepareImpl(String sql) throws SQLException {
            this.sql = sql;
            statement = connection.prepareStatement(sql);
        }


        public void execute() throws SQLException {
            try {
                statement.execute();
            }
            catch (SQLException e) {
                LOG.error("Erreur lors de l'exécution de la requête : " + sql, e);
                throw e;
            }
            finally {
                statement.close();
            }
        }


        public SqlTransactionalExecutor then() throws SQLException {
            return SqlTransactionalExecutor.this;
        }


        public SqlPrepare withNull(int sqlType) throws SQLException {
            statement.setNull(nextIndex++, sqlType);
            return this;
        }


        public SqlPrepare withBoolean(boolean value) throws SQLException {
            statement.setBoolean(nextIndex++, value);
            return this;
        }


        public SqlPrepare withByte(byte value) throws SQLException {
            statement.setByte(nextIndex++, value);
            return this;
        }


        public SqlPrepare withShort(short value) throws SQLException {
            statement.setShort(nextIndex++, value);
            return this;
        }


        public SqlPrepareImpl withInt(int value) throws SQLException {
            statement.setInt(nextIndex++, value);
            return this;
        }


        public SqlPrepare withLong(long value) throws SQLException {
            statement.setLong(nextIndex++, value);
            return this;
        }


        public SqlPrepare withFloat(float value) throws SQLException {
            statement.setFloat(nextIndex++, value);
            return this;
        }


        public SqlPrepare withDouble(double value) throws SQLException {
            statement.setDouble(nextIndex++, value);
            return this;
        }


        public SqlPrepare withBigDecimal(BigDecimal value) throws SQLException {
            statement.setBigDecimal(nextIndex++, value);
            return this;
        }


        public SqlPrepare withString(String value) throws SQLException {
            statement.setString(nextIndex++, value);
            return this;
        }


        public SqlPrepare withBytes(byte[] value) throws SQLException {
            statement.setBytes(nextIndex++, value);
            return this;
        }


        public SqlPrepare withDate(Date value) throws SQLException {
            statement.setDate(nextIndex++, value);
            return this;
        }


        public SqlPrepare withTime(Time value) throws SQLException {
            statement.setTime(nextIndex++, value);
            return this;
        }


        public SqlPrepare withTimestamp(Timestamp value) throws SQLException {
            statement.setTimestamp(nextIndex++, value);
            return this;
        }


        public SqlPrepareImpl withAsciiStream(InputStream inputStream, int length) throws SQLException {
            statement.setAsciiStream(nextIndex++, inputStream, length);
            return this;
        }


        public SqlPrepare withBinaryStream(InputStream value, int length) throws SQLException {
            statement.setBinaryStream(nextIndex++, value, length);
            return this;
        }


        public SqlPrepare withObject(Object value, int targetSqlType, int scale) throws SQLException {
            statement.setObject(nextIndex++, value, targetSqlType, scale);
            return this;
        }


        public SqlPrepare withObject(Object value, int targetSqlType) throws SQLException {
            statement.setObject(nextIndex++, value, targetSqlType);
            return this;
        }


        public SqlPrepare withObject(Object value) throws SQLException {
            statement.setObject(nextIndex++, value);
            return this;
        }


        public SqlPrepare withCharacterStream(Reader reader, int length) throws SQLException {
            statement.setCharacterStream(nextIndex++, reader, length);
            return this;
        }


        public SqlPrepare withRef(Ref value) throws SQLException {
            statement.setRef(nextIndex++, value);
            return this;
        }


        public SqlPrepare withBlob(Blob value) throws SQLException {
            statement.setBlob(nextIndex++, value);
            return this;
        }


        public SqlPrepare withClob(Clob value) throws SQLException {
            statement.setClob(nextIndex++, value);
            return this;
        }


        public SqlPrepare withArray(Array value) throws SQLException {
            statement.setArray(nextIndex++, value);
            return this;
        }


        public SqlPrepare withDate(Date value, Calendar cal) throws SQLException {
            statement.setDate(nextIndex++, value, cal);
            return this;
        }


        public SqlPrepare withTime(Time value, Calendar cal) throws SQLException {
            statement.setTime(nextIndex++, value, cal);
            return this;
        }


        public SqlPrepare withTimestamp(Timestamp value, Calendar cal) throws SQLException {
            statement.setTimestamp(nextIndex++, value, cal);
            return this;
        }


        public SqlPrepare withNull(int sqlType, String typeName) throws SQLException {
            statement.setNull(nextIndex++, sqlType, typeName);
            return this;
        }


        public SqlPrepare withURL(URL value) throws SQLException {
            statement.setURL(nextIndex++, value);
            return this;
        }
    }
}
