package ro.mycode.solarsyncbroker.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solarsync.simulation")
public record SimulationProperties(
        long seed,
        int tickSeconds,
        int realToVirtualRatio
) {
}
