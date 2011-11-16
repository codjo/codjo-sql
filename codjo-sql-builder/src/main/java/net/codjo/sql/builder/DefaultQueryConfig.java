/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Implémentation par défaut de QueryConfig.
 */
public class DefaultQueryConfig implements QueryConfig {
    private String rootTableName;
    private Map<String, JoinKey> joinKeyMap = new HashMap<String, JoinKey>();
    private JoinKeyExpression rootExpression;
    private OrderByField[] orderByFields = new OrderByField[0];


    public void setRootTableName(String rootTableName) {
        this.rootTableName = rootTableName;
    }


    public Map<String, JoinKey> getJoinKeyMap() {
        return Collections.unmodifiableMap(joinKeyMap);
    }


    private boolean areLinked(JoinKey joinKey, String tableName) {
        return tableName.equals(joinKey.getLeftTableName())
               || tableName.equals(joinKey.getRightTableName());
    }


    private String findOther(JoinKey joinKey, String tableName) {
        if (tableName.equals(joinKey.getLeftTableName())) {
            return joinKey.getRightTableName();
        }
        else {
            return joinKey.getLeftTableName();
        }
    }


    public String getRootTableName() {
        return rootTableName;
    }


    public JoinKeyExpression getRootExpression() {
        return rootExpression;
    }


    public OrderByField[] getOrderByFields() {
        return orderByFields;
    }

    public void setOrderByFields(OrderByField[] orderByFields){
        this.orderByFields = orderByFields;
    }

    public void setRootExpression(JoinKeyExpression rootExpression) {
        this.rootExpression = rootExpression;
    }


    public void add(JoinKey joinKey) {
        final boolean leftRelated = joinKeyMap.containsKey(joinKey.getLeftTableName());
        final boolean rightRelated = joinKeyMap.containsKey(joinKey.getRightTableName());
        final boolean rootRelated = areLinked(joinKey, rootTableName);

        if (!leftRelated && !rightRelated && !rootRelated) {
            throw new IllegalArgumentException("Noeud non rattaché au graphe de jointure");
        }
        if ((rootRelated && (leftRelated || rightRelated))
            || (leftRelated && rightRelated)) {
            throw new IllegalArgumentException("Création d'un cycle");
        }

        if (leftRelated && !rightRelated) {
            joinKeyMap.put(joinKey.getRightTableName(), joinKey);
        }
        else if (rightRelated) {
            joinKeyMap.put(joinKey.getLeftTableName(), joinKey);
        }
        else {
            joinKeyMap.put(findOther(joinKey, rootTableName), joinKey);
        }
    }
}
