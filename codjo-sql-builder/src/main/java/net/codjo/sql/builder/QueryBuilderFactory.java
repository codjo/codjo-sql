/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Factory de QueryBuilder.
 */
public final class QueryBuilderFactory {
    private QueryBuilderFactory() {
    }


    /**
     * Construction d'un QueryBuilder permettant de construire des requetes select.
     *
     * @param config confguration du QueryBuilder.
     *
     * @return un QueryBuilder.
     */
    public static QueryBuilder newSelectQueryBuilder(QueryConfig config) {
        return new SelectQueryBuilder(config);
    }
}
