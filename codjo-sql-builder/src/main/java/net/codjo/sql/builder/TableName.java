/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Description d'un nom de table (avec ou sans alias).
 */
public class TableName {
    private String fullTableName = null;


    public TableName(String fullTableName) {
        if (fullTableName == null || fullTableName.length() == 0) {
            throw new IllegalArgumentException("Nom de table incorrect: >"
                                               + fullTableName + "<");
        }
        this.fullTableName = fullTableName;
    }


    /**
     * Retourne le nom phisique de la table.
     *
     * <p> Tables temporaires: le caractere '#' n'est pas supprimé. </p>
     *
     * @return L'alias du nom de table
     */
    public String getDBTableName() {
        if (isAlias()) {
            return fullTableName.substring(0, fullTableName.indexOf("as")).trim();
        }
        return fullTableName;
    }


    /**
     * Retourne l'alias du nom de la table s'il existe sinon le nom phisique de la table.
     *
     * @return L'alias du nom de table
     */
    public String getAlias() {
        if (isAlias()) {
            return fullTableName.substring(fullTableName.indexOf("as") + 2).trim();
        }
        return fullTableName;
    }


    /**
     * Retourne le nom phisique de la table et son alias s'il existe.
     *
     * @return La valeur de fullTableName
     */
    public String getFullTableName() {
        return this.fullTableName;
    }


    private boolean isAlias() {
        return fullTableName.contains("as");
    }
}
