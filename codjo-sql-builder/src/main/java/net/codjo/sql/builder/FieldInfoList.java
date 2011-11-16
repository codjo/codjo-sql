/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
/**
 * Liste de FieldInfo.
 *
 * <p> <b>ATTENTION</b> : Tous les FieldInfo contenus dans la liste doivent avoir un alias unique. </p>
 */
public interface FieldInfoList {
    int size();


    FieldInfo getFieldInfo(int idx);
}
