/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Classe de test de <code>XmlQueryConfigListTest</code>.
 */
public class XmlQueryConfigListTest extends TestCase {
    public void test_constructor() throws Exception {
        XmlQueryConfigList configList = XmlQueryConfigList.newXmlQueryConfigList(
              XmlQueryConfigList.class.getResourceAsStream("XmlQueryConfigListTest.xml"));

        assertEquals(3, configList.size());

        QueryConfig config;
        JoinKey joinKey;

        // Config 1
        config = configList.getConfig("config1");
        Map jkMap = config.getJoinKeyMap();

        assertNotNull(config);
        assertEquals("AP_ROOT", config.getRootTableName());

        assertEquals("Nb de clef de jointure", 3, jkMap.size());

        joinKey = assertJoinKey(jkMap, "AP_A", "AP_ROOT", JoinKey.Type.INNER, "AP_A");
        assertEquals(1, joinKey.getPartList().size());
        assertPart(joinKey.getPartList(), 0, "COL_R1", "=", "COL_A1");

        joinKey = assertJoinKey(jkMap, "AP_B", "AP_A", JoinKey.Type.LEFT, "AP_B");
        assertEquals(2, joinKey.getPartList().size());
        assertPart(joinKey.getPartList(), 0, "COL_A1", "=", "COL_B1");
        assertPart(joinKey.getPartList(), 1, "COL_A2", "<", "COL_B2");

        joinKey = assertJoinKey(jkMap, "AP_C", "AP_C", JoinKey.Type.RIGHT, "AP_ROOT");
        assertEquals(1, joinKey.getPartList().size());
        assertPart(joinKey.getPartList(), 0, "COL_C1", ">=", "COL_R1");

        // Config 2
        config = configList.getConfig("config2");
        jkMap = config.getJoinKeyMap();
        assertEquals("AP_ROOT_2", config.getRootTableName());

        joinKey = assertJoinKey(jkMap, "AP_T", "AP_ROOT_2", JoinKey.Type.LEFT, "AP_T");
        assertEquals(1, joinKey.getPartList().size());
        assertPart(joinKey.getPartList(), 0, "COL_R1", "=", "COL_A1");

        // Config 3
        config = configList.getConfig("config3");
        QueryBuilder builder = QueryBuilderFactory.newSelectQueryBuilder(config);

// Création de la liste des colonnes utilisées
        DefaultFieldInfoList fieldList = new DefaultFieldInfoList();
        fieldList.add(new FieldInfo(new TableName("AP_ROOT_3")
              , "SHARE_PRICE_ID", "alias_1"));

// Construction de la requête select.
        String monSelect = builder.buildQuery(fieldList);

        assertEquals("select AP_ROOT_3.SHARE_PRICE_ID as alias_1 from AP_ROOT_3", monSelect);

//        assertPart(joinKey.getPartList(), 0, "COL_R1", "=", "COL_A1");
    }


    private void assertPart(List partList, int partIndex, String leftColumn,
                            String operator, String rightColumn) {
        JoinKey.Part part = ((JoinKey.Part)partList.get(partIndex));
        assertEquals(leftColumn, part.getLeftColumn());
        assertEquals(operator, part.getOperator());
        assertEquals(rightColumn, part.getRightColumn());
    }


    private JoinKey assertJoinKey(Map joinKeyMap, String mapTable, String leftTableName,
                                  JoinKey.Type joinType, String rightTableName) {
        JoinKey joinKey = (JoinKey)joinKeyMap.get(mapTable);

        assertNotNull("Jointure pour " + mapTable + " est présente", joinKey);
        assertEquals(leftTableName, joinKey.getLeftTableName());
        assertEquals(joinType, joinKey.getJoinType());
        assertEquals(rightTableName, joinKey.getRightTableName());
        return joinKey;
    }
}
