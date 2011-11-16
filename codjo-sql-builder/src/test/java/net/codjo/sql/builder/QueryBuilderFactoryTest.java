/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import junit.framework.TestCase;
/**
 * Classe de test de QueryBuilderFactory.
 */
public class QueryBuilderFactoryTest extends TestCase {
    public void test_newSelectFactory() {
        DefaultQueryConfig config = new DefaultQueryConfig();
        config.setRootTableName("R");
        config.add(new JoinKey("R", JoinKey.Type.INNER, "A"));

        QueryBuilder builder = QueryBuilderFactory.newSelectQueryBuilder(config);
        assertEquals(SelectQueryBuilder.class, builder.getClass());

        assertNotSame(builder, QueryBuilderFactory.newSelectQueryBuilder(config));
    }
}
