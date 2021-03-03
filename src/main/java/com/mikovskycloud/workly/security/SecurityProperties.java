package com.mikovskycloud.workly.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "workly.security")
public class SecurityProperties {

    String secret;

    String tokenPrefix;

    String headerString;

    Long expirationTime;

}
