package net.codjo.sql.server.util;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.After;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.stubVoid;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SqlTransactionalExecutorTest {
    private static final String DELETE_STATEMENT = "delete from PM_SEC_MODEL";
    private static final String INSERT_STATEMENT = "insert into PM_SEC_MODEL (VERSION, MODEL) values (?, ?)";
    private Connection connection = mock(Connection.class);
    private PreparedStatement deleteStatement = mock(PreparedStatement.class);
    private PreparedStatement insertStatement = mock(PreparedStatement.class);


    @Before
    public void setUp() throws Exception {
        stub(connection.getAutoCommit()).toReturn(true);
        stub(connection.prepareStatement(DELETE_STATEMENT)).toReturn(deleteStatement);
        stub(connection.prepareStatement(INSERT_STATEMENT)).toReturn(insertStatement);
    }


    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(connection, deleteStatement, insertStatement);
    }


    @Test
    public void test_nominal() throws Exception {
        SqlTransactionalExecutor.init(connection)
              .prepare(DELETE_STATEMENT).then()
              .prepare(INSERT_STATEMENT)
              .withInt(0)
              .withAsciiStream(new ByteArrayInputStream("<xml></xml>".getBytes()), 11).then()
              .execute();

        verify(connection).prepareStatement(DELETE_STATEMENT);
        verify(connection).prepareStatement(INSERT_STATEMENT);
        verify(insertStatement).setInt(1, 0);
        verify(insertStatement).setAsciiStream(Mockito.eq(2),
                                               Mockito.<InputStream>anyObject(),
                                               Mockito.eq(11));

        verify(connection).getAutoCommit();
        verify(connection).setAutoCommit(false);
        verify(deleteStatement).execute();
        verify(deleteStatement).close();
        verify(insertStatement).execute();
        verify(insertStatement).close();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }


    @Test
    public void test_exceptionInPrepare() throws Exception {
        SQLException expectedException = new SQLException();
        stub(deleteStatement.execute()).toThrow(expectedException);

        try {
            SqlTransactionalExecutor.init(connection)
                  .prepare(DELETE_STATEMENT).then()
                  .execute();
            fail();
        }
        catch (SQLException e) {
            assertSame(expectedException, e);
        }

        verify(connection).prepareStatement(DELETE_STATEMENT);
        verify(connection).getAutoCommit();
        verify(connection).setAutoCommit(false);
        verify(deleteStatement).execute();
        verify(deleteStatement).close();
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }


    @Test
    public void test_exceptionInCommit() throws Exception {
        SQLException expectedException = new SQLException();
        stubVoid(connection).toThrow(expectedException).on().commit();

        try {
            SqlTransactionalExecutor.init(connection)
                  .prepare(DELETE_STATEMENT).then()
                  .execute();
            fail();
        }
        catch (SQLException e) {
            assertSame(expectedException, e);
        }

        verify(connection).prepareStatement(DELETE_STATEMENT);
        verify(connection).getAutoCommit();
        verify(connection).setAutoCommit(false);
        verify(deleteStatement).execute();
        verify(deleteStatement).close();
        verify(connection).commit();
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }
}
