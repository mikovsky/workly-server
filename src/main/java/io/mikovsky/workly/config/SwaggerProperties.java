package io.mikovsky.workly.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SwaggerProperties {

    @Value("${workly.swagger.contact.name}")
    private String contactName;

    @Value("${workly.swagger.contact.url}")
    private String contactUrl;

    @Value("${workly.swagger.contact.email}")
    private String contactEmail;

    @Value("${workly.swagger.apinfo.title}")
    private String apiInfoTitle;

    @Value("${workly.swagger.apinfo.description}")
    private String apiInfoDescription;

    @Value("${workly.swagger.apinfo.version}")
    private String apiInfoVersion;

    @Value("${workly.swagger.host}")
    private String host;

    @Value("${workly.swagger.protocol}")
    private String protocol;

}
