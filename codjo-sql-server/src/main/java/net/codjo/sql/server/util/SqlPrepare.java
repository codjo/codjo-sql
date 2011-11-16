package net.codjo.sql.server.util;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public interface SqlPrepare {
    SqlTransactionalExecutor then() throws SQLException;


    SqlPrepare withNull(int sqlType) throws SQLException;


    SqlPrepare withBoolean(boolean x) throws SQLException;


    SqlPrepare withByte(byte x) throws SQLException;


    SqlPrepare withShort(short x) throws SQLException;


    SqlPrepare withInt(int x) throws SQLException;


    SqlPrepare withLong(long x) throws SQLException;


    SqlPrepare withFloat(float x) throws SQLException;


    SqlPrepare withDouble(double x) throws SQLException;


    SqlPrepare withBigDecimal(BigDecimal x) throws SQLException;


    SqlPrepare withString(String x) throws SQLException;


    SqlPrepare withBytes(byte x[]) throws SQLException;


    SqlPrepare withDate(Date x) throws SQLException;


    SqlPrepare withTime(Time x) throws SQLException;


    SqlPrepare withTimestamp(Timestamp x) throws SQLException;


    SqlPrepare withAsciiStream(java.io.InputStream x, int length) throws SQLException;


    SqlPrepare withBinaryStream(java.io.InputStream x, int length) throws SQLException;


    SqlPrepare withObject(Object x, int targetSqlType, int scale) throws SQLException;


    SqlPrepare withObject(Object x, int targetSqlType) throws SQLException;


    SqlPrepare withObject(Object x) throws SQLException;


    SqlPrepare withCharacterStream(Reader reader, int length) throws SQLException;


    SqlPrepare withRef(Ref x) throws SQLException;


    SqlPrepare withBlob(Blob x) throws SQLException;


    SqlPrepare withClob(Clob x) throws SQLException;


    SqlPrepare withArray(Array x) throws SQLException;


    SqlPrepare withDate(Date x, Calendar cal) throws SQLException;


    SqlPrepare withTime(Time x, Calendar cal) throws SQLException;


    SqlPrepare withTimestamp(Timestamp x, Calendar cal) throws SQLException;


    SqlPrepare withNull(int sqlType, String typeName) throws SQLException;


    SqlPrepare withURL(URL x) throws SQLException;
}
