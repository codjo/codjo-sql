/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.AgentMock;
import net.codjo.agent.ServiceHelper;
import junit.framework.TestCase;
/**
 * Classe de test de {@link JdbcService}.
 */
public class JdbcServiceTest extends TestCase {

    public void test_getName() throws Exception {
        JdbcService service = new JdbcService(new JdbcManagerMock());
        assertEquals(JdbcServiceHelper.NAME, service.getName());
    }


    public void test_getServiceHelper() throws Exception {
        JdbcService jdbcService = new JdbcService(new JdbcManagerMock());
        ServiceHelper actual = jdbcService.getServiceHelper(new AgentMock());
        assertNotNull(actual);
        assertTrue(actual instanceof JdbcServiceHelper);
    }
}
