/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
/**
 * Cette classe permet de generer un ordre SQL de selection a partir d'une liste de <code>FieldInfo</code>.
 */
class SelectQueryBuilder implements QueryBuilder {
    private static final Logger APP = Logger.getLogger(SelectQueryBuilder.class);
    private QueryConfig config;


    SelectQueryBuilder(QueryConfig preference) {
        this.config = preference;
    }


    /**
     * Genere un ordre SQL de selection a partir de la table <code>AP_SHARE_PRICE</code> filtre par la table
     * <code>selectionTable</code>.<p>Les champs de selection et lors tables respectives sont donnes par la
     * liste de <code>FieldInfo</code>.</p>
     *
     * @param fileColumnGenerator Liste de FieldInfo
     *
     * @return L'ordre SQL de selection
     *
     * @throws IllegalArgumentException Erreur
     * @since Broadcaster 1.0
     */
    public String buildQuery(FieldInfoList fileColumnGenerator) {
        if (fileColumnGenerator == null || fileColumnGenerator.size() == 0) {
            throw new IllegalArgumentException("Le tableau de 'FieldInfo' n'a pas ete initialise");
        }
        Query query = new Query();
        appendSelectAndGroupByClause(query, fileColumnGenerator);
        setFromClause(query, fileColumnGenerator);
        appendWhereAndHavingAndExtraGroupByClause(query, fileColumnGenerator);
        appendOrderByClause(query);
        return query.buildQuery();
    }


    private void addAndClause(QueryPartPointer sql, JKPart[] joinKey, int currentJoinKeyIdx,
                              List<JKPart> usedJoinKey) {
        StringBuffer buffer = new StringBuffer(sql.getSql());
        for (int y = currentJoinKeyIdx + 1; y < joinKey.length; y++) {
            if (joinKey[y].samePart(joinKey[currentJoinKeyIdx]) && !usedJoinKey.contains(joinKey[y])) {
                usedJoinKey.add(joinKey[y]);
                buffer.append(" and (").append(joinKey[y].getExpression()).append(')');
            }
        }
        sql.setSql(buffer.toString());
    }


    private void appendSelectAndGroupByClause(Query query, FieldInfoList fileColumnGenerator) {
        List<String> fieldList = new ArrayList<String>();

        for (int i = 0; i < fileColumnGenerator.size(); i++) {
            FieldInfo fieldInfo = fileColumnGenerator.getFieldInfo(i);
            if (!fieldList.contains(fieldInfo.getAlias())) {
                fieldList.add(fieldInfo.getAlias());
                String selectField = fieldInfo.getFullDBName();
                String aliasField = fieldInfo.getAlias();
                query.appendSelectClause(selectField, aliasField);
                query.appendGroupByClause(selectField);
            }
        }
    }


    private void appendWhereAndHavingAndExtraGroupByClause(Query query, FieldInfoList fileColumnGenerator) {
        Map map = getUsedJoinKeyMap(fileColumnGenerator);
        if (config.getRootExpression() != null) {
            appendRootExpressionStuff(query);
        }
        for (Object joinKey : map.values()) {
            JKPart[] joinKeys = ((JoinKey)joinKey).toJKParts();
            if (joinKeys.length > 0) {
                query.appendWhereClause(joinKeys[0].getWhereClause());
            }
            if (joinKeys.length > 0 && joinKeys[0].getHavingClause() != null) {
                query.appendHavingClause(joinKeys[0].getHavingClause());
                String[] extraGroupByField = joinKeys[0].getExtraGroupByFields();
                for (String anExtraGroupByField : extraGroupByField) {
                    query.appendGroupByClause(anExtraGroupByField);
                }
            }
        }
    }


    private void appendOrderByClause(Query query) {
        OrderByField[] orderByFields = config.getOrderByFields();
        if (orderByFields == null || orderByFields.length == 0) {
            return;
        }

        query.appendOrderByClause(orderByFields);
    }


    private void appendRootExpressionStuff(Query query) {
        query.appendWhereClause(config.getRootExpression().getWhereClause());
        if (config.getRootExpression().getHavingClause() != null) {
            query.appendHavingClause(config.getRootExpression().getHavingClause());
            String[] extraGroupByField = config.getRootExpression().getExtraGroupByFields();
            for (String anExtraGroupByField : extraGroupByField) {
                query.appendGroupByClause(anExtraGroupByField);
            }
        }
    }


    private String determineSqlPart(String tableName, Map partPointersMap) {
        if (partPointersMap.get(tableName) != null) {
            return ((QueryPartPointer)partPointersMap.get(tableName)).getSql();
        }
        else {
            return tableName;
        }
    }


    private Map<String, JoinKey> getUsedJoinKeyMap(FieldInfoList fileColumnGenerator) {
        // Cas ou on a une seule table : la root table
        if (config.getJoinKeyMap().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, JoinKey> result = buildTableMap(getUsedTables(fileColumnGenerator));

        if (APP.isDebugEnabled()) {
            APP.debug("usedJoinKeyMap : " + result);
        }

        return result;
    }


    /**
     * Retourne une List&lt;String&gt; contenant le nom de toutes les tables associées à tous les FieldInfo du
     * fileColumnGenerator. Cette méthode est utilisée uniquement par le getUsedJoinKeyMap.
     *
     * @param fileColumnGenerator le FileGenerator duquel on veut le nom des tables
     *
     * @return les tables du file generator
     */
    private List<String> getUsedTables(FieldInfoList fileColumnGenerator) {
        List<String> tableList = new ArrayList<String>();
        tableList.add(config.getRootTableName());
        for (int i = 0; i < fileColumnGenerator.size(); i++) {
            String fullTableName = fileColumnGenerator.getFieldInfo(i).getFullDBTableName();
            if (!tableList.contains(fullTableName)) {
                tableList.add(fullTableName);
            }
        }

        addJoinedKeyUsedTables(tableList);
        return tableList;
    }


    /**
     * Ajoute dans la liste les tables utilisées en jointure.  Cette méthode utilise une liste parce qu'il
     * n'est pas possible d'itérer sur un set pendant qu'on en modifie les éléments.  On pourrait la remplacé
     * par une méthode manipulant récursivement des Set.
     *
     * @throws IllegalArgumentException TODO TODO Cette méthode devrait être remplacée une interface de
     *                                  Collector, et une classe de Collector qui s'assure la méthode de
     *                                  l'interface est appelé une et une seule fois pour chaque objet.
     */
    private void addJoinedKeys(List<String> usedTable) {
        Map<String, JoinKey> joinKeyMap = config.getJoinKeyMap();

        for (int i = 0; i < usedTable.size(); i++) {
            String tableName = usedTable.get(i);

            final JoinKey joinKey = joinKeyMap.get(tableName);
            if (joinKey == null) {
                // @ugly Traitement du cas particulier de la table racine
                if (tableName.equals(config.getRootTableName())) {
                    continue;
                }

                // end ugly
                throw new IllegalArgumentException("La table: >" + tableName
                                                   + "< n'est pas une table parametrée pour la requête");
            }

            for (JKPart jkPart : joinKey.toJKParts()) {
                if (!usedTable.contains(jkPart.getLeftTableName())) {
                    usedTable.add(jkPart.getLeftTableName());
                }
                if (!usedTable.contains(jkPart.getRightTableName())) {
                    usedTable.add(jkPart.getRightTableName());
                }
            }
        }
    }


    private void addJoinedKeyUsedTables(List<String> usedTable) {
        JoinKeyExpression rootExpression = config.getRootExpression();
        if (rootExpression != null) {
            Map<String, JoinKey> joinKeyMap = config.getJoinKeyMap();
            for (String tableName : joinKeyMap.keySet()) {
                JoinKey joinKey = joinKeyMap.get(tableName);
                if (joinKey == null) {
                    return;
                }
                for (JKPart jkPart : joinKey.toJKParts()) {
                    addJoinedKeyTables(rootExpression, jkPart, usedTable);
                }
            }
        }

        addJoinedKeys(usedTable);
    }


    private void addJoinedKeyTables(JoinKeyExpression rootExpression, JKPart jkPart, List<String> usedTable) {
        String leftTableName = jkPart.getLeftTableName();
        String rightTableName = jkPart.getRightTableName();

        String whereClause = rootExpression.getWhereClause();
        if (whereClause != null) {
            addUsedTables(whereClause, leftTableName, rightTableName, usedTable);
        }

        String havingClause = rootExpression.getHavingClause();
        if (havingClause != null) {
            addUsedTables(havingClause, leftTableName, rightTableName, usedTable);
        }

        String[] groupByFields = rootExpression.getExtraGroupByFields();
        if (groupByFields != null) {
            for (String groupByField : groupByFields) {
                addUsedTables(groupByField, leftTableName, rightTableName, usedTable);
            }
        }
    }


    private void addUsedTables(String sqlClause, String leftTableName, String rightTableName,
                               List<String> usedTable) {
        if (sqlClause.contains(leftTableName) && !usedTable.contains(leftTableName)) {
            usedTable.add(leftTableName);
        }
        if (sqlClause.contains(rightTableName) && !usedTable.contains(rightTableName)) {
            usedTable.add(rightTableName);
        }
    }


    /**
     * Construit une map contenant comme clef le nom des tables et comme valeur ????
     *
     * @return TODO Cette méthode devrait être renmplacé par un appel à un Transformer
     *         (org.apache.commons.collections.Transformer). /coteg
     */
    private Map<String, JoinKey> buildTableMap(List<String> tableNames) {
        Map<String, JoinKey> joinKeyMap = config.getJoinKeyMap();

        Map<String, JoinKey> result = new HashMap<String, JoinKey>();
        for (String tableName : tableNames) {
            // @ugly Traitement du cas particulier de la table racine
            if (tableName.equals(config.getRootTableName())) {
                continue;
            }

            // end ugly
            result.put(tableName, joinKeyMap.get(tableName));
        }

        return result;
    }


    private void setFromClause(Query query, FieldInfoList fileColumnGenerator) {
        Map<String, QueryPartPointer> partPointersMap = new HashMap<String, QueryPartPointer>();

        Map<String, JoinKey> joinKeys = getUsedJoinKeyMap(fileColumnGenerator);
        addJoinKeysForOrderByFieldsIfNeeded(joinKeys);

        //  Cas ou on a une seule table : la root table
        if (joinKeys.isEmpty()) {
            query.setFromClause(config.getRootTableName());
            return;
        }
        List<JKPart> usedJoinKey = new ArrayList<JKPart>();
        QueryPartPointer sql = new QueryPartPointer();

        for (JoinKey joinKey : joinKeys.values()) {
            JKPart[] jkParts = joinKey.toJKParts();
            for (int i = 0; i < jkParts.length; i++) {
                if (!usedJoinKey.contains(jkParts[i])) {
                    String leftTableName = jkParts[i].getLeftTableName();
                    String rightTableName = jkParts[i].getRightTableName();
                    sql = new QueryPartPointer();
                    sql.setSql("(" + determineSqlPart(leftTableName, partPointersMap)
                               + jkParts[i].getJoinType() + determineSqlPart(rightTableName, partPointersMap)
                               + " on (" + jkParts[i].getExpression() + ")");

                    usedJoinKey.add(jkParts[i]);

                    addAndClause(sql, jkParts, i, usedJoinKey);

                    if (joinKey.getJoinKeyExpression().getExtraOnClause() != null) {
                        sql.setSql(sql.getSql() + " " + joinKey.getJoinKeyExpression().getExtraOnClause());
                    }
                    sql.setSql(sql.getSql() + ")");

                    updatePartPointersMap(partPointersMap, leftTableName, rightTableName, sql);
                }
            }
        }
        query.setFromClause(sql.getSql());
    }


    private void addJoinKeysForOrderByFieldsIfNeeded(Map<String, JoinKey> joinKeys) {
        Map<String, JoinKey> allJoinKeys = config.getJoinKeyMap();

        Set<String> tableNames = allJoinKeys.keySet();
        for (String tableName : tableNames) {
            TableName fullTableName = new TableName(tableName);
            String refName = fullTableName.getAlias();
            OrderByField[] orderByFields = config.getOrderByFields();
            for (OrderByField orderByField : orderByFields) {
                if (orderByField.getTableName().equals(refName)) {
                    joinKeys.put(tableName, allJoinKeys.get(tableName));
                }
            }
        }
    }


    private void updatePartPointersMap(Map<String, QueryPartPointer> partPointersMap, String leftTableName,
                                       String rightTableName, QueryPartPointer sql) {
        if (partPointersMap.get(leftTableName) == null && partPointersMap.get(rightTableName) == null) {
            partPointersMap.put(leftTableName, sql);
            partPointersMap.put(rightTableName, sql);
        }
        else if (partPointersMap.get(leftTableName) != null && partPointersMap.get(rightTableName) == null) {
            QueryPartPointer leftPart = partPointersMap.get(leftTableName);
            leftPart.connectTo(sql);
            partPointersMap.put(rightTableName, leftPart);
        }
        else if (partPointersMap.get(leftTableName) == null && partPointersMap.get(rightTableName) != null) {
            QueryPartPointer rightPart = partPointersMap.get(rightTableName);
            rightPart.connectTo(sql);
            partPointersMap.put(leftTableName, rightPart);
        }
        else {
            partPointersMap.get(leftTableName).connectTo(sql);
            partPointersMap.get(rightTableName).connectTo(sql);
        }
    }


    class Query {
        private String fromClause = null;
        private StringBuffer groupByClause = new StringBuffer();
        private StringBuffer orderByClause = new StringBuffer();
        private StringBuffer havingClause = new StringBuffer();
        private StringBuffer selectClause = new StringBuffer();
        private StringBuffer whereClause = new StringBuffer();


        public void appendGroupByClause(String groupBy) {
            if (this.groupByClause.length() > 0) {
                this.groupByClause.append(" , ");
            }
            this.groupByClause.append(groupBy);
        }


        public void appendOrderByClause(OrderByField[] orderByFields) {
            for (OrderByField orderByField : orderByFields) {
                if (this.orderByClause.length() > 0) {
                    this.orderByClause.append(" , ");
                }
                this.orderByClause.append(orderByField.getFullName());
            }
        }


        public void appendHavingClause(String having) {
            if (this.havingClause.length() > 0) {
                this.havingClause.append(" and ");
            }
            this.havingClause.append(having);
        }


        public void appendSelectClause(String selectField, String aliasField) {
            if (this.selectClause.length() > 0) {
                this.selectClause.append(" , ");
            }
            this.selectClause.append(selectField).append(" as ").append(aliasField);
        }


        public void appendWhereClause(String where) {
            if (where == null) {
                return;
            }
            if (this.whereClause.length() > 0) {
                this.whereClause.append(" and ");
            }
            this.whereClause.append(where);
        }


        public String buildQuery() {
            StringBuffer query =
                  new StringBuffer("select ").append(this.selectClause).append(" from ")
                        .append(this.fromClause)
                        .append(getWhereClause())
                        .append(getGroupByClause())
                        .append(getHavingClause())
                        .append(getOrderByClause());
            return query.toString();
        }


        public void setFromClause(String fromClause) {
            this.fromClause = fromClause;
        }


        private String getGroupByClause() {
            if (this.havingClause.length() == 0) {
                return "";
            }
            String groupByReturn = this.groupByClause.toString();
            if (groupByReturn.length() > 0) {
                return " group by " + groupByReturn;
            }
            return groupByReturn;
        }


        private String getOrderByClause() {
            String orderByReturn = this.orderByClause.toString();
            if (orderByReturn.length() > 0) {
                return " order by " + orderByReturn;
            }
            return orderByReturn;
        }


        private String getHavingClause() {
            String havingReturn = this.havingClause.toString();
            if (havingReturn.length() > 0) {
                return " having " + havingReturn;
            }
            return havingReturn;
        }


        private String getWhereClause() {
            String whereReturn = this.whereClause.toString();
            if (whereReturn.length() > 0) {
                return " where " + whereReturn;
            }
            return whereReturn;
        }
    }

    private static class QueryPartPointer {
        private String sql = null;
        private QueryPartPointer master = null;


        public void connectTo(QueryPartPointer newMaster) {
            if (master != null) {
                master.connectTo(newMaster);
            }
            else {
                this.master = newMaster;
            }
        }


        public String getSql() {
            if (master != null) {
                return master.getSql();
            }
            else {
                return this.sql;
            }
        }


        public void setSql(String newSql) {
            if (master != null) {
                master.setSql(newSql);
            }
            else {
                this.sql = newSql;
            }
        }
    }
}
