/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.UserId;
import net.codjo.test.common.LogString;
import org.junit.Before;
import org.junit.Test;
/**
 * Classe de test de {@link JdbcServiceHelper}.
 */
public class JdbcServiceHelperTest {
    private LogString log = new LogString();
    private JdbcServiceHelper jdbcServiceHelper;


    @Before
    public void setUp() throws Exception {
        JdbcManagerMock managerMock = new JdbcManagerMock(log);
        jdbcServiceHelper = new JdbcServiceHelper(managerMock);
    }


    @Test
    public void test_delegate() throws Exception {
        jdbcServiceHelper.createPool(UserId.createId("boissie", "**"), "mint_dbo", "mint_dbo");
        log.assertContent("createPool(boissie, mint_dbo, mint_dbo)");
        log.clear();

        jdbcServiceHelper.destroyPool(null);
        log.assertContent("destroyPool(null)");
        log.clear();

        jdbcServiceHelper.getPool(null);
        log.assertContent("getPool(null)");
        log.clear();
    }
}
