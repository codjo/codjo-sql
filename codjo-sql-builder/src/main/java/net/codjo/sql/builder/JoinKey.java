/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * Designe une clef de jointure entre 2 tables.
 */
public class JoinKey {
    private JoinKeyExpression joinKeyExpression = new JoinKeyExpression();
    private String leftTableName;
    private String rightTableName;
    private List<Part> partList = new ArrayList<Part>();
    private Type joinType = Type.INNER;


    public JoinKey(String leftTableName, Type joinType, String rightTableName) {
        this.leftTableName = leftTableName;
        this.rightTableName = rightTableName;
        this.joinType = joinType;
    }


    public List getPartList() {
        return Collections.unmodifiableList(partList);
    }


    public Type getJoinType() {
        return joinType;
    }


    public String getLeftTableName() {
        return leftTableName;
    }


    public String getRightTableName() {
        return rightTableName;
    }


    public void setJoinKeyExpression(JoinKeyExpression expression) {
        this.joinKeyExpression = expression;
    }


    public void addPart(Part part) {
        partList.add(part);
    }


    JKPart[] toJKParts() {
        JKPart[] result = new JKPart[partList.size()];

        int index = 0;
        for (Iterator<Part> iter = partList.iterator(); iter.hasNext(); index++) {
            Part part = iter.next();

            result[index] =
                  new JKPart(joinKeyExpression, buildPartClause(part), leftTableName,
                             rightTableName, joinType.toSql());
        }

        return result;
    }


    private String buildPartClause(Part part) {
        return getTableAlias(leftTableName) + "." + part.leftColumn
               + " " + part.operator
               + " " + getTableAlias(rightTableName) + "." + part.rightColumn;
    }


    private static boolean isAlias(String tableName) {
        return (tableName.contains("as"));
    }


    private static String getTableAlias(String tableName) {
        if (isAlias(tableName)) {
            return tableName.substring(tableName.indexOf("as") + 2).trim();
        }
        return tableName;
    }


    public String buildJoinClause() {
        StringBuffer buffer = new StringBuffer();
        for (Part part : partList) {
            if (buffer.length() > 0) {
                buffer.append(" and ");
            }
            buffer.append(buildPartClause(part));
        }
        if (joinKeyExpression.getExtraOnClause() != null) {
            buffer.append(' ').append(joinKeyExpression.getExtraOnClause());
        }
        return buffer.toString();
    }


    public JoinKeyExpression getJoinKeyExpression() {
        return joinKeyExpression;
    }


    public void setExtraOnClause(String clause) {
        joinKeyExpression.setExtraOnClause(clause);
    }


    /**
     * Partie d'une jointure (eg 'COL_A = COL_B').
     */
    public static final class Part {
        private final String leftColumn;
        private final String rightColumn;
        private final String operator;


        public Part(String leftColumn, String operator, String rightColumn) {
            this.leftColumn = leftColumn;
            this.operator = operator;
            this.rightColumn = rightColumn;
        }


        public Part(String column) {
            this(column, "=", column);
        }


        public String getLeftColumn() {
            return leftColumn;
        }


        public String getRightColumn() {
            return rightColumn;
        }


        public String getOperator() {
            return operator;
        }
    }
    /**
     * Enumeration de type de jointure.
     */
    public static final class Type {
        public static final Type LEFT = new Type("LEFT");
        public static final Type RIGHT = new Type("RIGHT");
        public static final Type INNER = new Type("INNER");
        private final String myName; // for debug only


        private Type(String name) {
            myName = name;
        }


        @Override
        public String toString() {
            return myName;
        }


        String toSql() {
            if (this == LEFT) {
                return JKPart.LEFT_JOIN;
            }
            else if (this == RIGHT) {
                return JKPart.RIGHT_JOIN;
            }
            else if (this == INNER) {
                return JKPart.INNER_JOIN;
            }
            else {
                throw new IllegalStateException();
            }
        }
    }
}
