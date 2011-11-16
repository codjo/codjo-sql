/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Description d'une colonne.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class FieldInfo {
    public static final String DEFAULT_ALIAS_PREFIX = "COL_";
    private String alias = null;
    private String field = null;
    private TableName table = null;


    public FieldInfo(TableName table, String field, String alias) {
        if (table == null) {
            throw new IllegalArgumentException("TableName n'a pas ete initialise");
        }
        assertValid("Nom du champ", field);
        assertValid("Alias", alias);
        this.table = table;
        this.field = field;
        this.alias = alias;
    }


    public FieldInfo(TableName tableName, String fieldName, int position) {
        this(tableName, fieldName, DEFAULT_ALIAS_PREFIX + position);
    }


    private void assertValid(final String label, String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(label + " incorrect: >" + value + "<");
        }
    }


    @Override
    public boolean equals(Object parm1) {
        if (parm1 instanceof FieldInfo) {
            FieldInfo right = (FieldInfo)parm1;
            return this.getFullDBName().equals(right.getFullDBName());
        }
        return super.equals(parm1);
    }


    public String getAlias() {
        return alias;
    }


    public String getDBFieldName() {
        return field;
    }


    public String getDBTableName() {
        return table.getDBTableName();
    }


    public String getFullDBName() {
        return table.getAlias() + "." + this.field;
    }


    public String getFullDBTableName() {
        return table.getFullTableName();
    }


    @Override
    public int hashCode() {
        return this.getFullDBName().hashCode();
    }


    @Override
    public String toString() {
        return getAlias();
    }
}
