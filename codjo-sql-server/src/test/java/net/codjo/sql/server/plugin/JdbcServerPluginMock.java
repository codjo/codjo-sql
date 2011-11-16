package net.codjo.sql.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.plugin.common.ApplicationCore;
/**
 *
 */
public class JdbcServerPluginMock extends JdbcServerPlugin {
    public JdbcServerPluginMock() {
        super(new ApplicationCore() {
            @Override
            protected AgentContainer createAgentContainer(ContainerConfiguration configuration) {
                return null;
            }
        });
    }
}
