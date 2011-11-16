/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Interface designant un objet permettant de construire une requete.
 */
public interface QueryBuilder {
    /**
     * Genere un ordre SQL adapté à la FieldInfoList.
     *
     * <p> Les champs de selection et leurs tables respectives sont donnes par la liste de
     * <code>FieldInfo</code>. </p>
     *
     * @param fieldList Liste de FieldInfo
     *
     * @return L'ordre SQL.
     */
    public String buildQuery(FieldInfoList fieldList);
}
