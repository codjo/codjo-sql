/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Expression where et having d'une jointure.
 */
public final class JoinKeyExpression {
    private String[] extraGroupByFields;
    private String havingClause = null;
    private String whereClause = null;
    private String extraOnClause;


    public JoinKeyExpression() {
    }


    public JoinKeyExpression(String whereClause) {
        this(whereClause, null, (String[])null);
    }


    public JoinKeyExpression(String whereClause, String havingClause,
                             String extraGroupByField) {
        this(whereClause, havingClause, new String[]{extraGroupByField});
    }


    public JoinKeyExpression(String whereClause, String havingClause,
                             String[] extraGroupByFields) {
        this.whereClause = whereClause;
        this.havingClause = havingClause;
        this.extraGroupByFields = extraGroupByFields;
    }


    public String[] getExtraGroupByFields() {
        return extraGroupByFields;
    }


    public String getHavingClause() {
        return havingClause;
    }


    public String getWhereClause() {
        return whereClause;
    }


    public JoinKeyExpression setExtraOnClause(String clause) {
        this.extraOnClause = clause;
        return this;
    }


    public String getExtraOnClause() {
        return extraOnClause;
    }


    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }


    public void setHavingClause(String havingClause) {
        this.havingClause = havingClause;
    }


    public void setExtraGroupByFields(String... extraGroupByFields) {
        this.extraGroupByFields = extraGroupByFields;
    }
}
