package ro.mycode.solarsyncbroker.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solarsync.agent")
public record AgentProperties(
        AgentMode mode,
        int planningIntervalSeconds
) {
}
