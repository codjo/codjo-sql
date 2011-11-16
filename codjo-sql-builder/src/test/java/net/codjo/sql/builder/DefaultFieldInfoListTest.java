/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Classe de test de DefaultFieldInfoList.
 *
 * @version $Revision: 1.2 $
 */
public class DefaultFieldInfoListTest extends TestCase {
    private DefaultFieldInfoList list;
    private TableName table;


    public void test_add() {
        FieldInfo fieldA = new FieldInfo(table, "field", "Alias_1");
        FieldInfo fieldB = new FieldInfo(table, "field", "Alias_2");

        list.add(fieldA);
        list.add(fieldB);

        assertEquals(2, list.size());
        assertEquals(fieldA, list.getFieldInfo(0));
        assertEquals(fieldB, list.getFieldInfo(1));
    }


    public void test_add_sameAlias_notSameCol() {
        FieldInfo fieldA = new FieldInfo(table, "fieldA", "Alias_1");
        FieldInfo fieldB = new FieldInfo(table, "fieldB", "Alias_1");

        list.add(fieldA);

        try {
            list.add(fieldB);
            fail("fieldB possède le même alias que fieldA mais pas sur la même colonne !");
        }
        catch (IllegalArgumentException ex) {
        }

        assertEquals(1, list.size());
        assertEquals(fieldA, list.getFieldInfo(0));
    }


    public void test_add_sameAlias_notSameTable() {
        FieldInfo fieldA = new FieldInfo(table, "fieldA", "Alias_1");
        FieldInfo fieldB = new FieldInfo(new TableName("OTHER"), "fieldA", "Alias_1");

        list.add(fieldA);

        try {
            list.add(fieldB);
            fail("fieldB possède le même alias que fieldA mais pas sur la même colonne !");
        }
        catch (IllegalArgumentException ex) {
        }

        assertEquals(1, list.size());
        assertEquals(fieldA, list.getFieldInfo(0));
    }


    public void test_add_sameAlias_sameCol() {
        FieldInfo fieldA = new FieldInfo(table, "fieldA", "Alias_1");
        FieldInfo fieldB = new FieldInfo(table, "fieldA", "Alias_1");

        list.add(fieldA);
        list.add(fieldB);

        assertEquals(1, list.size());
        assertSame(fieldA, list.getFieldInfo(0));
    }


    @Override
    protected void setUp() throws Exception {
        list = new DefaultFieldInfoList();
        table = new TableName("TABLE");
    }
}
