/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Classe de test <code>JoinKey</code>.
 */
public class JoinKeyTest extends TestCase {
    private JoinKeyExpression joinKeyExpression = new JoinKeyExpression();


    public void test_buildJoinClause() {
        JoinKey jk = new JoinKey("AP_SELECTION", JoinKey.Type.INNER, "PM_BROADCAST");
        jk.addPart(new JoinKey.Part("COLUMNS_ID", ">", "ID"));
        jk.addPart(new JoinKey.Part("SECTION_ID"));
        jk.setJoinKeyExpression(joinKeyExpression);

        String clause = jk.buildJoinClause();

        assertEquals("AP_SELECTION.COLUMNS_ID > PM_BROADCAST.ID "
                     + "and AP_SELECTION.SECTION_ID = PM_BROADCAST.SECTION_ID", clause);
    }


    public void test_buildJoinClause_withExtraOnClause() {
        JoinKey jk = new JoinKey("AP_PRODUCT", JoinKey.Type.LEFT, "AP_PORTFOLIO");
        jk.addPart(new JoinKey.Part("PORTFOLIO_ID", "=", "ID"));
        jk.setExtraOnClause("and AP_PRODUCT.CURRENCY = 'EUR'");

        String clause = jk.buildJoinClause();

        assertEquals("AP_PRODUCT.PORTFOLIO_ID = AP_PORTFOLIO.ID and AP_PRODUCT.CURRENCY = 'EUR'", clause);
    }


    public void test_buildJoinClause_withAddedOnStatements() {
        joinKeyExpression.setExtraOnClause("and AP_SELECTION.SECTION_ID = '69'");

        JoinKey jk = new JoinKey("AP_SELECTION", JoinKey.Type.INNER, "PM_BROADCAST");
        jk.addPart(new JoinKey.Part("COLUMNS_ID", ">", "ID"));
        jk.setJoinKeyExpression(joinKeyExpression);

        String clause = jk.buildJoinClause();

        assertEquals("AP_SELECTION.COLUMNS_ID > PM_BROADCAST.ID "
                     + "and AP_SELECTION.SECTION_ID = '69'", clause);
    }


    public void test_toJKParts() {
        JoinKey jk = new JoinKey("AP_SELECTION", JoinKey.Type.INNER, "PM_BROADCAST");
        jk.addPart(new JoinKey.Part("COLUMNS_ID", ">", "ID"));
        jk.addPart(new JoinKey.Part("SECTION_ID"));
        jk.setJoinKeyExpression(joinKeyExpression);

        JKPart[] array = jk.toJKParts();

        assertEquals(2, array.length);
        assertCommonValue(joinKeyExpression, array[0], "AP_SELECTION");
        assertEquals("AP_SELECTION.COLUMNS_ID > PM_BROADCAST.ID", array[0].getExpression());

        assertCommonValue(joinKeyExpression, array[1], "AP_SELECTION");
        assertEquals("AP_SELECTION.SECTION_ID = PM_BROADCAST.SECTION_ID", array[1].getExpression());
    }


    public void test_toJKParts_withAlias() {
        JoinKey jk = new JoinKey("AP_SELECTION as sel", JoinKey.Type.INNER, "PM_BROADCAST");
        jk.addPart(new JoinKey.Part("SECTION_ID"));
        jk.setJoinKeyExpression(joinKeyExpression);

        JKPart[] array = jk.toJKParts();

        assertEquals(1, array.length);
        assertCommonValue(joinKeyExpression, array[0], "AP_SELECTION as sel");
        assertEquals("sel.SECTION_ID = PM_BROADCAST.SECTION_ID", array[0].getExpression());
    }


    private void assertCommonValue(JoinKeyExpression expression, JKPart partA, String leftTable) {
        assertEquals(leftTable, partA.getLeftTableName());
        assertEquals("PM_BROADCAST", partA.getRightTableName());
        assertEquals(JKPart.INNER_JOIN, partA.getJoinType());

        assertEquals(expression.getExtraGroupByFields(), partA.getExtraGroupByFields());
        assertEquals(expression.getHavingClause(), partA.getHavingClause());
        assertEquals(expression.getWhereClause(), partA.getWhereClause());
    }
}
