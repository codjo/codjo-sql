package net.codjo.sql.builder;
import java.util.Arrays;
import junit.framework.TestCase;
/**
 */
public class JoinKeyExpressionTest extends TestCase {
    public void test_setters() throws Exception {
        JoinKeyExpression expression = new JoinKeyExpression();

        expression.setExtraOnClause("ee");
        assertEquals("ee", expression.getExtraOnClause());

        expression.setWhereClause("ee");
        assertEquals("ee", expression.getWhereClause());

        expression.setHavingClause("ee");
        assertEquals("ee", expression.getHavingClause());

        expression.setExtraGroupByFields("ee");
        assertEquals("[ee]", Arrays.asList(expression.getExtraGroupByFields()).toString());
    }
}
