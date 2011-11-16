/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Tests de la classe <code>SelectQueryBuilderTest</code>.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1 $
 */
public class SelectQueryBuilderTest extends TestCase {
    private static final String BROADCAST_TABLE = "PM_BROADCAST_COLUMNS";
    private static final String BROADCAST_SEL_TABLE = "PM_BROADCAST_SECTION";
    private static final String SELECTION_TABLE = "#COLUMNS_LIST";
    private static final String COMPUTED_TABLE = "#COMPUTED";
    private DefaultQueryConfig config;
    private String selection;
    private String computed;


    @Override
    protected void setUp() throws Exception {
        selection = SELECTION_TABLE;
        computed = COMPUTED_TABLE;

        DefaultQueryConfig defaultConfig = new DefaultQueryConfig();
        defaultConfig.setRootTableName(SELECTION_TABLE);
        JoinKey jk;

        // Jointure 1
        jk = new JoinKey(SELECTION_TABLE, JoinKey.Type.INNER, BROADCAST_TABLE);
        jk.addPart(new JoinKey.Part("COLUMNS_ID"));
        jk.addPart(new JoinKey.Part("SECTION_ID"));
        defaultConfig.add(jk);

        // Jointure 2
        jk = new JoinKey(BROADCAST_TABLE, JoinKey.Type.LEFT, "PM_BROADCAST_SECTION");
        jk.addPart(new JoinKey.Part("SECTION_ID"));
        defaultConfig.add(jk);

        // Jointure 3
        jk = new JoinKey(COMPUTED_TABLE, JoinKey.Type.INNER, SELECTION_TABLE);
        jk.addPart(new JoinKey.Part("SELECTION_ID"));
        defaultConfig.add(jk);

        // Jointure 4
        jk = new JoinKey("VL_SELECT", JoinKey.Type.RIGHT, BROADCAST_TABLE);
        jk.addPart(new JoinKey.Part("VL", "=", "FIELD_A"));
        defaultConfig.add(jk);

        // Jointure 5
        jk = new JoinKey("COB as COB_1", JoinKey.Type.RIGHT, "VL_SELECT");
        jk.addPart(new JoinKey.Part("ID_COB", "=", "VL"));
        jk.setJoinKeyExpression(new JoinKeyExpression("(COB_1.DATE_COB <= VL_SELECT.DATE)",
                                                      "(Max(COB_1.DATE_COB) = COB.DATE_COB)",
                                                      "COB.DATE_COB"));
        defaultConfig.add(jk);

        // Jointure 6
        jk = new JoinKey("COB", JoinKey.Type.RIGHT, "COB as COB_1");
        jk.addPart(new JoinKey.Part("ID_COB"));
        defaultConfig.add(jk);

        // Jointure 7
        jk = new JoinKey(BROADCAST_TABLE, JoinKey.Type.INNER, "NEW_OPER");
        jk.addPart(new JoinKey.Part("BRANCH_TABLE_A", "=", "BRANCH_TABLE_B"));
        jk.addPart(new JoinKey.Part("DATE_TABLE_A", ">=", "DATE_DEBUT_TABLE_B"));
        jk.addPart(new JoinKey.Part("DATE_TABLE_A", "<=", "DATE_FIN_TABLE_B"));
        defaultConfig.add(jk);

        // Jointure 8
        jk = new JoinKey("T_A", JoinKey.Type.RIGHT, BROADCAST_TABLE);
        jk.addPart(new JoinKey.Part("FIELD_B", "=", "FIELD_A"));
        defaultConfig.add(jk);

        // Jointure 9
        jk = new JoinKey("REF as REF_1", JoinKey.Type.RIGHT, "T_A");
        jk.addPart(new JoinKey.Part("FIELD_F", "=", "FIELD_E"));
        defaultConfig.add(jk);

        // Jointure 10
        jk = new JoinKey("T_B", JoinKey.Type.RIGHT, BROADCAST_TABLE);
        jk.addPart(new JoinKey.Part("FIELD_D", "=", "FIELD_C"));
        defaultConfig.add(jk);

        // Jointure 11
        jk = new JoinKey("REF as REF_2", JoinKey.Type.RIGHT, "T_B");
        jk.addPart(new JoinKey.Part("FIELD_H", "=", "FIELD_J"));
        defaultConfig.add(jk);

        // Jointure 12
        jk = new JoinKey("REF as REF_3", JoinKey.Type.RIGHT, "T_B");
        jk.addPart(new JoinKey.Part("FIELD_H", "=", "FIELD_I"));
        jk.setJoinKeyExpression(new JoinKeyExpression().setExtraOnClause("and REF_3.LABEL='5'"));
        defaultConfig.add(jk);

        this.config = defaultConfig;
    }


    public void test_buildQuery() {
        DefaultFieldInfoList usedFieldList = new DefaultFieldInfoList();
        usedFieldList.add(new FieldInfo(new TableName(selection), "SHARE_PRICE_ID", 1));
        usedFieldList.add(new FieldInfo(new TableName(BROADCAST_TABLE), "DB_TABLE_NAME", 2));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);

        String resultSelect =
              "select " + selection
              + ".SHARE_PRICE_ID as COL_1 , PM_BROADCAST_COLUMNS.DB_TABLE_NAME as COL_2 from (" + selection
              + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))";
        assertEquals(resultSelect, dqb.buildQuery(usedFieldList));
        assertEquals("verification bis (bug)", resultSelect, dqb.buildQuery(usedFieldList));
    }


    public void test_buildQuery_OneTable() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName(BROADCAST_TABLE), "DB_TABLE_NAME", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);

        String resultSelect =
              "select PM_BROADCAST_COLUMNS.DB_TABLE_NAME as COL_1 from (" + selection
              + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_buildQuery_OneTable_defaultExpression() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName(BROADCAST_TABLE), "DB_TABLE_NAME", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);

        String resultSelect =
              "select PM_BROADCAST_COLUMNS.DB_TABLE_NAME as COL_1 from (" + selection
              + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))";

        // Avec JoinKeyExpression + clause Where
        ((DefaultQueryConfig)config).setRootExpression(new JoinKeyExpression("A=B"));
        assertEquals(resultSelect + " where A=B", dqb.buildQuery(fileColumnGenerator));

        // Avec JoinKeyExpression - clause Where
        ((DefaultQueryConfig)config).setRootExpression(new JoinKeyExpression(null));
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));

        // Avec JoinKeyExpression - clause Where + having
        ((DefaultQueryConfig)config).setRootExpression(new JoinKeyExpression(null, "havA", "A"));
        assertEquals(resultSelect + " group by PM_BROADCAST_COLUMNS.DB_TABLE_NAME , A having havA",
                     dqb.buildQuery(fileColumnGenerator));
    }


    public void test_buildQuery_addJoinKeyWithWhereClause() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName(SELECTION_TABLE), "DB_TABLE_NAME", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);

        String selectExpression = "select " + SELECTION_TABLE + ".DB_TABLE_NAME as COL_1 from ";
        String joinExpression = "(" + selection
                                + " inner join PM_BROADCAST_COLUMNS"
                                + " on (" + selection + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID)"
                                + " and (" + selection + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))";

        // Avec JoinKeyExpression avec clause Where
        ((DefaultQueryConfig)config).setRootExpression(new JoinKeyExpression(BROADCAST_SEL_TABLE + ".A=B"));
        String expectedQuery = selectExpression + "(" + joinExpression
                               + " left join " + BROADCAST_SEL_TABLE
                               + " on (" + BROADCAST_TABLE + ".SECTION_ID = " + BROADCAST_SEL_TABLE
                               + ".SECTION_ID))"
                               + " where " + BROADCAST_SEL_TABLE + ".A=B";
        assertEquals(expectedQuery, dqb.buildQuery(fileColumnGenerator));

        // Avec JoinKeyExpression sans clause Where
        ((DefaultQueryConfig)config).setRootExpression(new JoinKeyExpression(null));
        expectedQuery = selectExpression + selection;
        assertEquals(expectedQuery, dqb.buildQuery(fileColumnGenerator));

        // Avec JoinKeyExpression sans clause Where avec having
        ((DefaultQueryConfig)config)
              .setRootExpression(new JoinKeyExpression(null, BROADCAST_TABLE + ".A", "A"));
        expectedQuery = selectExpression + joinExpression
                        + " group by " + SELECTION_TABLE + ".DB_TABLE_NAME , A"
                        + " having " + BROADCAST_TABLE + ".A";
        assertEquals(expectedQuery, dqb.buildQuery(fileColumnGenerator));

        // Avec JoinKeyExpression sans clause Where avec having
        ((DefaultQueryConfig)config)
              .setRootExpression(
                    new JoinKeyExpression(null, BROADCAST_TABLE + ".A", BROADCAST_SEL_TABLE + ".A"));
        expectedQuery = selectExpression + "(" + joinExpression
                        + " left join " + BROADCAST_SEL_TABLE
                        + " on (" + BROADCAST_TABLE + ".SECTION_ID = " + BROADCAST_SEL_TABLE + ".SECTION_ID))"
                        + " group by " + SELECTION_TABLE + ".DB_TABLE_NAME , " + BROADCAST_SEL_TABLE + ".A"
                        + " having " + BROADCAST_TABLE + ".A";
        assertEquals(expectedQuery, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_buildQuery_all_Tables() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("PM_BROADCAST_SECTION"), "SECTION_NAME", 1));
        fileColumnGenerator.add(new FieldInfo(new TableName(computed), "DATE_HEURE", 2));
        fileColumnGenerator.add(new FieldInfo(new TableName(BROADCAST_TABLE), "DB_TABLE_NAME", 3));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);

        String resultSelect =
              "select PM_BROADCAST_SECTION.SECTION_NAME as COL_1 , " + computed
              + ".DATE_HEURE as COL_2 , PM_BROADCAST_COLUMNS.DB_TABLE_NAME as COL_3 from (" + computed
              + " inner join ((" + selection + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID)) left join PM_BROADCAST_SECTION on (PM_BROADCAST_COLUMNS.SECTION_ID = PM_BROADCAST_SECTION.SECTION_ID)) on ("
              + computed + ".SELECTION_ID = " + selection + ".SELECTION_ID))";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
        assertEquals("verification bis (bug)", resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_buildQuery_maxValuationDate() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("COB"), "COB_FIELD", 1));
        fileColumnGenerator.add(new FieldInfo(new TableName("COB"), "DATE_COB", 2));
        fileColumnGenerator.add(new FieldInfo(new TableName("COB"), "ID_COB", 3));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);
        String querybuilt = dqb.buildQuery(fileColumnGenerator);
        String resultSelect =
              "select COB.COB_FIELD as COL_1 , COB.DATE_COB as COL_2 , COB.ID_COB as COL_3 from ((COB right join COB as COB_1 on (COB.ID_COB = COB_1.ID_COB)) right join (VL_SELECT right join ("
              + selection + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID)) on (VL_SELECT.VL = PM_BROADCAST_COLUMNS.FIELD_A)) on (COB_1.ID_COB = VL_SELECT.VL)) where (COB_1.DATE_COB <= VL_SELECT.DATE) group by COB.COB_FIELD , COB.DATE_COB , COB.ID_COB , COB.DATE_COB having (Max(COB_1.DATE_COB) = COB.DATE_COB)";
        assertEquals(resultSelect, querybuilt);
    }


    public void test_buildQuery_multiLink() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("REF_REFERENTIAL as REF_RISK"), "LABEL", 1));
        fileColumnGenerator.add(new FieldInfo(new TableName("REF_REFERENTIAL as REF_FUND"), "LABEL", 2));

        SelectQueryBuilder dqb = new SelectQueryBuilder(newConfigMultiLink());
        String querybuilt = dqb.buildQuery(fileColumnGenerator);
        String resultSelect =
              "select REF_RISK.LABEL as COL_1 , REF_FUND.LABEL as COL_2"
              + " from ((((AP_COMMERCIAL as AP_COMMERCIAL_1"
              + " inner join (#COLUMNS_LIST inner join AP_FUND_PRICE"
              + " on (#COLUMNS_LIST.PORTFOLIO_BRANCH = AP_FUND_PRICE.PORTFOLIO_BRANCH)"
              + " and (#COLUMNS_LIST.VALUATION_DATE = AP_FUND_PRICE.VALUATION_DATE))"
              + " on (AP_COMMERCIAL_1.PORTFOLIO_CODE = AP_FUND_PRICE.PORTFOLIO_CODE))"
              + " inner join AP_COMMERCIAL on (AP_COMMERCIAL_1.PORTFOLIO_CODE = AP_COMMERCIAL.PORTFOLIO_CODE))"
              + " inner join REF_REFERENTIAL as REF_RISK on (AP_COMMERCIAL.RISK_LEVEL = REF_RISK.ID))"
              + " inner join REF_REFERENTIAL as REF_FUND on (AP_COMMERCIAL.FUND_TYPE = REF_FUND.ID))"
              + " group by REF_RISK.LABEL , REF_FUND.LABEL , AP_COMMERCIAL.DATE_BEGIN"
              + " having (Max(AP_COMMERCIAL_1.DATE_BEGIN) = AP_COMMERCIAL.DATE_BEGIN)";
        assertEquals(resultSelect, querybuilt);
    }


    public void test_buildQuery_new_operator() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("NEW_OPER"), "COB_FIELD", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);
        String resultSelect =
              "select NEW_OPER.COB_FIELD as COL_1 from ((" + selection
              + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))"
              + " inner join NEW_OPER on (PM_BROADCAST_COLUMNS.BRANCH_TABLE_A = NEW_OPER.BRANCH_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A >= NEW_OPER.DATE_DEBUT_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A <= NEW_OPER.DATE_FIN_TABLE_B))";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_buildQuery_tableAlias() {
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("REF as REF_1"), "FIELD_1", 1));
        fileColumnGenerator.add(new FieldInfo(new TableName("REF as REF_2"), "FIELD_2", 2));
        fileColumnGenerator.add(new FieldInfo(new TableName("REF as REF_3"), "FIELD_3", 3));
        fileColumnGenerator.add(new FieldInfo(new TableName(COMPUTED_TABLE), "DATE_HEURE", 4));
        SelectQueryBuilder dqb = new SelectQueryBuilder(config);
        String resultSelect =
              "select REF_1.FIELD_1 as COL_1 , REF_2.FIELD_2 as COL_2 , REF_3.FIELD_3 as COL_3 ,"
              + " #COMPUTED.DATE_HEURE as COL_4 "
              + "from (REF as REF_2 right join (#COMPUTED inner join ((REF as REF_3 "
              + "right join T_B on (REF_3.FIELD_H = T_B.FIELD_I) and REF_3.LABEL='5') "
              + "right join (REF as REF_1 right join (#COLUMNS_LIST "
              + "inner join (T_A right join PM_BROADCAST_COLUMNS "
              + "on (T_A.FIELD_B = PM_BROADCAST_COLUMNS.FIELD_A)) "
              + "on (#COLUMNS_LIST.COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) "
              + "and (#COLUMNS_LIST.SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID)) "
              + "on (REF_1.FIELD_F = T_A.FIELD_E)) on (T_B.FIELD_D = PM_BROADCAST_COLUMNS.FIELD_C)) "
              + "on (#COMPUTED.SELECTION_ID = #COLUMNS_LIST.SELECTION_ID)) on (REF_2.FIELD_H = T_B.FIELD_J))";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_selectOrderByWithFieldsAlreadyThereInSelectFields() throws Exception {
        config.setOrderByFields(new OrderByField[]{
              new OrderByField("NEW_OPER", "COB_FIELD")
        });
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("NEW_OPER"), "COB_FIELD", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);
        String resultSelect =
              "select NEW_OPER.COB_FIELD as COL_1 from ((" + selection
              + " inner join PM_BROADCAST_COLUMNS on (" + selection
              + ".COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID) and (" + selection
              + ".SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))"
              + " inner join NEW_OPER on (PM_BROADCAST_COLUMNS.BRANCH_TABLE_A = NEW_OPER.BRANCH_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A >= NEW_OPER.DATE_DEBUT_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A <= NEW_OPER.DATE_FIN_TABLE_B))"
              + " order by NEW_OPER.COB_FIELD";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    public void test_selectOrderByWithFieldsNotAlreadyThereInSelectFields() throws Exception {
        config.setOrderByFields(new OrderByField[]{
              new OrderByField("NEW_OPER", "COB_FIELD"),
              new OrderByField("T_A", "FIELD_B"),
              new OrderByField("REF_1", "FIELD_F")
        });
        DefaultFieldInfoList fileColumnGenerator = new DefaultFieldInfoList();
        fileColumnGenerator.add(new FieldInfo(new TableName("NEW_OPER"), "COB_FIELD", 1));

        SelectQueryBuilder dqb = new SelectQueryBuilder(config);
        String resultSelect =
              "select NEW_OPER.COB_FIELD as COL_1 from ((#COLUMNS_LIST"
              + " inner join (REF as REF_1 right join (T_A right join PM_BROADCAST_COLUMNS"
              + " on (T_A.FIELD_B = PM_BROADCAST_COLUMNS.FIELD_A))"
              + " on (REF_1.FIELD_F = T_A.FIELD_E))"
              + " on (#COLUMNS_LIST.COLUMNS_ID = PM_BROADCAST_COLUMNS.COLUMNS_ID)"
              + " and (#COLUMNS_LIST.SECTION_ID = PM_BROADCAST_COLUMNS.SECTION_ID))"
              + " inner join NEW_OPER on (PM_BROADCAST_COLUMNS.BRANCH_TABLE_A = NEW_OPER.BRANCH_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A >= NEW_OPER.DATE_DEBUT_TABLE_B)"
              + " and (PM_BROADCAST_COLUMNS.DATE_TABLE_A <= NEW_OPER.DATE_FIN_TABLE_B))"
              + " order by NEW_OPER.COB_FIELD , T_A.FIELD_B , REF_1.FIELD_F";
        assertEquals(resultSelect, dqb.buildQuery(fileColumnGenerator));
    }


    private DefaultQueryConfig newConfigMultiLink() {
        DefaultQueryConfig configMultiLink = new DefaultQueryConfig();
        configMultiLink.setRootTableName(SELECTION_TABLE);

        JoinKey jk;

        // Jointure
        jk = new JoinKey(SELECTION_TABLE, JoinKey.Type.INNER, "AP_FUND_PRICE");
        jk.addPart(new JoinKey.Part("PORTFOLIO_BRANCH"));
        jk.addPart(new JoinKey.Part("VALUATION_DATE"));
        configMultiLink.add(jk);

        // Jointure
        jk = new JoinKey(COMPUTED_TABLE, JoinKey.Type.INNER, SELECTION_TABLE);
        jk.addPart(new JoinKey.Part("SELECTION_ID"));
        configMultiLink.add(jk);

        // Jointure
        jk = new JoinKey("AP_COMMERCIAL as AP_COMMERCIAL_1", JoinKey.Type.INNER, "AP_FUND_PRICE");
        jk.addPart(new JoinKey.Part("PORTFOLIO_CODE"));
        jk.setJoinKeyExpression(new JoinKeyExpression(null,
                                                      "(Max(AP_COMMERCIAL_1.DATE_BEGIN) = AP_COMMERCIAL.DATE_BEGIN)",
                                                      "AP_COMMERCIAL.DATE_BEGIN"));
        configMultiLink.add(jk);

        // Jointure
        jk = new JoinKey("AP_COMMERCIAL as AP_COMMERCIAL_1", JoinKey.Type.INNER, "AP_COMMERCIAL");
        jk.addPart(new JoinKey.Part("PORTFOLIO_CODE"));
        configMultiLink.add(jk);

        // Jointure
        jk = new JoinKey("AP_COMMERCIAL", JoinKey.Type.INNER, "REF_REFERENTIAL as REF_RISK");
        jk.addPart(new JoinKey.Part("RISK_LEVEL", "=", "ID"));
        configMultiLink.add(jk);

        // Jointure
        jk = new JoinKey("AP_COMMERCIAL", JoinKey.Type.INNER, "REF_REFERENTIAL as REF_FUND");
        jk.addPart(new JoinKey.Part("FUND_TYPE", "=", "ID"));
        configMultiLink.add(jk);

        return configMultiLink;
    }
}
