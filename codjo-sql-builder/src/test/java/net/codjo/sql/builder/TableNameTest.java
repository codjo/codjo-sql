/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Classe de test de <code>TableName</code>.
 */
public class TableNameTest extends TestCase {
    public void test_constructor() {
        try {
            new TableName(null);
            fail("TableName null");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }
        try {
            new TableName("");
            fail("TableName vide");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }
    }


    public void test_alias() {
        TableName table = new TableName("TABLE as truc");
        assertEquals("truc", table.getAlias());

        table = new TableName("PM_TEST");
        assertEquals("PM_TEST", table.getAlias());
    }

    public void test_getDBTableName() {
        TableName tn = new TableName("PM_TEST as MY_TABLE");
        assertEquals("PM_TEST", tn.getDBTableName());

        tn = new TableName("PM_TEST");
        assertEquals("PM_TEST", tn.getDBTableName());
    }

    public void test_alias_tempTable() {
        TableName table = new TableName("#TABLE as truc");
        assertEquals("truc", table.getAlias());
        assertEquals("#TABLE", table.getDBTableName());
    }
}
