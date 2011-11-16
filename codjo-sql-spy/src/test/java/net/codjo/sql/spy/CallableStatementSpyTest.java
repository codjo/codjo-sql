package net.codjo.sql.spy;
import java.sql.CallableStatement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CallableStatementSpyTest extends ConnectionSpyTestCase {
    private CallableStatementSpy callableStatementSpy;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        CallableStatement callableStatement = Mockito.mock(CallableStatement.class);
        Mockito.stub(connection.prepareCall(Mockito.anyString())).toReturn(callableStatement);
        callableStatementSpy = (CallableStatementSpy)connectionSpy.prepareCall(
              "select 1 from AP_TEST where MY_COL = ?");
    }


    @Test
    public void test_setValue_null() throws Exception {
        callableStatementSpy.setString(1, null);
    }


    @Test
    public void test_executeQuery_missingParameter() throws Exception {
        callableStatementSpy.executeQuery();
    }
}
