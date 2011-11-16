package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 *
 */
public class OrderByFieldTest extends TestCase {

    public void test_nominal() throws Exception {
        OrderByField orderByField = new OrderByField("AP_PIPO", "TOTO");
        assertEquals("AP_PIPO", orderByField.getTableName());
        assertEquals("TOTO", orderByField.getFieldName());
        assertEquals("AP_PIPO.TOTO", orderByField.getFullName());
    }


    public void test_badParameters() throws Exception {
        try {
            new OrderByField(null, null);
            fail();
        }
        catch (Exception e) {
            assertEquals("tableName incorrect: >null<", e.getMessage());
        }
        
        try {
            new OrderByField(null, "toto");
            fail();
        }
        catch (Exception e) {
            assertEquals("tableName incorrect: >null<", e.getMessage());
        }

        try {
            new OrderByField("AP_PIPO", null);
            fail();
        }
        catch (Exception e) {
            assertEquals("fieldName incorrect: >null<", e.getMessage());
        }

        try {
            new OrderByField("", "");
            fail();
        }
        catch (Exception e) {
            assertEquals("tableName incorrect: ><", e.getMessage());
        }

        try {
            new OrderByField("", "toto");
            fail();
        }
        catch (Exception e) {
            assertEquals("tableName incorrect: ><", e.getMessage());
        }

        try {
            new OrderByField("AP_PIPO", "");
            fail();
        }
        catch (Exception e) {
            assertEquals("fieldName incorrect: ><", e.getMessage());
        }
    }
}
