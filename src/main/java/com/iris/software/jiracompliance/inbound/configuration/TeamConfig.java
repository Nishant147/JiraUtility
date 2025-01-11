package com.iris.software.jiracompliance.inbound.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class TeamConfig {
    private Map<String, String> teams;
}
