/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.server;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AgentMock;
import net.codjo.agent.ServiceException;
import net.codjo.agent.UserId;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
import junit.framework.TestCase;
/**
 * Classe de test de {@link JdbcServiceUtil}.
 */
public class JdbcServiceUtilTest extends TestCase {
    private JdbcServiceUtil serviceUtil;
    private LogString log = new LogString();


    public void test_getConnectionPool() throws Exception {
        ConnectionPoolMock connectionPoolMock = new ConnectionPoolMock();

        JdbcServiceHelperMock serviceHelper =
              new JdbcServiceHelperMock(new LogString("jdbcHelper", log));
        serviceHelper.mockGetPool(connectionPoolMock);

        AgentMock agent = new AgentMock(new LogString("agent", log));
        agent.mockGetHelper(serviceHelper);

        AclMessage message = new AclMessage(AclMessage.Performative.INFORM);
        message.encodeUserId(UserId.decodeUserId("l/6e594b55386d41376c37593d/0/1"));

        assertEquals(connectionPoolMock, serviceUtil.getConnectionPool(agent, message));

        log.assertContent("agent.getHelper(JDBCService.NAME), jdbcHelper.getPool(l/6e594b55386d41376c37593d/0/1)");
    }


    public void test_getConnectionPool_failure() throws Exception {
        AgentMock agent = new AgentMock(new LogString("agent", log));
        agent.mockGetHelperFailure(new ServiceException("badService"));

        try {
            serviceUtil.getConnectionPool(agent, null);
            fail();
        }
        catch (SQLException ex) {
            assertEquals("Impossible d'acceder au service JDBC : badService",
                         ex.getLocalizedMessage());
        }
    }


    @Override
    protected void setUp() throws Exception {
        serviceUtil = new JdbcServiceUtil();
    }
}
