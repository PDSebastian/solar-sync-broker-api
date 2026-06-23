package ro.mycode.solarsyncbroker.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solarsync.grid")
public record GridProperties(
        double transformerNominalKw,
        int safetyMarginPercent
) {
}
