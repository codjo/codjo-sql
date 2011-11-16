/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Classe de test de <code>FieldInfo</code>.
 */
public class FieldInfoTest extends TestCase {
    public void test_std() {
        FieldInfo fi = new FieldInfo(new TableName("table"), "field", 1);
        assertEquals("field", fi.getDBFieldName());
        assertEquals("table", fi.getDBTableName());

        assertEquals(fi.getAlias(), fi.toString());

        assertEquals(fi.getFullDBName().hashCode(), fi.hashCode());
    }


    public void test_constructor_tableName() {
        try {
            new FieldInfo(null, "fi", "alias");
            fail("TableName null");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }
    }


    public void test_constructor_field() {
        try {
            new FieldInfo(new TableName("pm_as"), null, "alias");
            fail("TableName null");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }

        try {
            new FieldInfo(new TableName("pm_as"), "", "alias");
            fail("TableName null");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }
    }


    public void test_constructor_alias() {
        try {
            new FieldInfo(new TableName("pm_as"), "ss", null);
            fail("alias null");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }

        try {
            new FieldInfo(new TableName("pm_as"), "ss", "");
            fail("alias empty");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }
    }


    public void test_equals() {
        FieldInfo fi = new FieldInfo(new TableName("PM_TEST as MY_TABLE"), "MY_FIELD", 1);
        FieldInfo fiSame =
            new FieldInfo(new TableName("PM_TEST as MY_TABLE"), "MY_FIELD", 2);

        assertEquals("Meme colonne de la meme table", fi, fiSame);

        assertEquals("Meme hashCode()", fi.hashCode(), fiSame.hashCode());

        assertEquals(false, fi.equals("toto"));
        assertEquals(false, fi.equals(null));
    }


    public void test_equals_notFieldInfo() {
        assertFalse(new FieldInfo(new TableName("ee"), "ee", 1).equals("notString"));
    }


    public void test_getAlias() {
        FieldInfo fi = new FieldInfo(new TableName("PM_TEST as MY_TABLE"), "MY_FIELD", 1);
        assertEquals("COL_1", fi.getAlias());

        fi = new FieldInfo(new TableName("PM_TEST"), "MY_FIELD", 5);
        assertEquals("COL_5", fi.getAlias());
    }


    public void test_getFullDBName() {
        FieldInfo fi = new FieldInfo(new TableName("PM_TEST as MY_TABLE"), "MY_FIELD", 1);
        assertEquals("", "MY_TABLE.MY_FIELD", fi.getFullDBName());

        fi = new FieldInfo(new TableName("PM_TEST"), "MY_FIELD", 1);
        assertEquals("", "PM_TEST.MY_FIELD", fi.getFullDBName());
    }
}
