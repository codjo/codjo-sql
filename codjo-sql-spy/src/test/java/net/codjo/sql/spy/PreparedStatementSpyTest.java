package net.codjo.sql.spy;
import java.sql.PreparedStatement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PreparedStatementSpyTest extends ConnectionSpyTestCase {
    private PreparedStatementSpy preparedStatementSpy;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        Mockito.stub(connection.prepareStatement(Mockito.anyString())).toReturn(preparedStatement);
        preparedStatementSpy = (PreparedStatementSpy)connectionSpy.prepareStatement(
              "select 1 from AP_TEST where MY_COL = ?");
    }


    @Test
    public void test_setValue_null() throws Exception {
        preparedStatementSpy.setString(1, null);
    }


    @Test
    public void test_executeQuery_missingParameter() throws Exception {
        preparedStatementSpy.executeQuery();
    }
}
