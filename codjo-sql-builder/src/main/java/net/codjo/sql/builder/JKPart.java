/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Définition d'une jointure entre deux tables.
 */
class JKPart {
    public static final String INNER_JOIN = " inner join ";
    public static final String LEFT_JOIN = " left join ";
    public static final String RIGHT_JOIN = " right join ";
    private String expression = null;
    private String joinType = null;
    private String leftTableName = null;
    private JoinKeyExpression queryExpression = null;
    private String rightTableName = null;


    JKPart(JoinKeyExpression queryExpression,
           String joinExpression,
           String leftTableName,
           String rightTableName,
           String joinType) {
        if (queryExpression == null
            || joinExpression == null
            || leftTableName == null
            || rightTableName == null
            || joinType == null) {
            throw new IllegalArgumentException("Parametres invalides");
        }

        this.expression = joinExpression;
        this.leftTableName = leftTableName;
        this.rightTableName = rightTableName;
        this.joinType = joinType;
        this.queryExpression = queryExpression;
    }


    public boolean samePart(JKPart keyMap) {
        return keyMap.getLeftTableName().equals(this.leftTableName)
               && keyMap.getRightTableName().equals(this.rightTableName);
    }


    public String getExpression() {
        return expression;
    }


    public String[] getExtraGroupByFields() {
        return this.queryExpression.getExtraGroupByFields();
    }


    public String getHavingClause() {
        return this.queryExpression.getHavingClause();
    }


    public String getJoinType() {
        return this.joinType;
    }


    public String getLeftTableName() {
        return leftTableName;
    }


    public String getRightTableName() {
        return rightTableName;
    }


    public String getWhereClause() {
        return this.queryExpression.getWhereClause();
    }


    @Override
    public String toString() {
        return "JKPart(" + getLeftTableName() + " " + getJoinType() + " "
               + getRightTableName() + ")";
    }
}
