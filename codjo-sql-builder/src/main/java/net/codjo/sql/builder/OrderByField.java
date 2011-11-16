package net.codjo.sql.builder;
/**
 *
 */
public class OrderByField {
    private String tableName;
    private String fieldName;
    private String fullName;


    public OrderByField(String tableName, String fieldName) {
        assertValid("tableName", tableName);
        assertValid("fieldName", fieldName);
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.fullName = tableName + "." + fieldName;
    }


    public String getFieldName() {
        return fieldName;
    }


    public String getTableName() {
        return tableName;
    }


    public String getFullName() {
        return fullName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderByField that = (OrderByField)o;

        if (!fieldName.equals(that.fieldName)) {
            return false;
        }
        if (!tableName.equals(that.tableName)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = 31 * result + fieldName.hashCode();
        return result;
    }


    private void assertValid(String label, String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(label + " incorrect: >" + value + "<");
        }
    }
}
