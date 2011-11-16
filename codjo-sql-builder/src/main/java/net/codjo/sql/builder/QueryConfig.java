/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.Map;
/**
 * Configuration d'objet constructeur de requête.
 */
public interface QueryConfig {
    /**
     * Retourne une table de hash permettant de determiner la clefs de jointure vers la table racine.
     *
     * <p> <b>NB:</b> La table racine n'est pas présente dans la map. </p>
     *
     * @return Map (key=string tableName, value=objet JoinKey)
     *
     * @see JoinKey
     */
    Map<String, JoinKey> getJoinKeyMap();


    /**
     * Retourne la table racine obligatoirement utilisée dans les requêtes.
     *
     * @return un nom de table.
     */
    String getRootTableName();


    /**
     * Retourne l'expression de jointure obligatoire qui est associé à la table racine.
     *
     * @return une JoinKeyExpression ou <code>null</code> si il n'y en a pas.
     */
    JoinKeyExpression getRootExpression();


    /**
     * Retourne la liste des champs à placer dans l'order by final
     *
     * @return Les champs à placer dans l'order by
     */
    OrderByField[] getOrderByFields();
}
