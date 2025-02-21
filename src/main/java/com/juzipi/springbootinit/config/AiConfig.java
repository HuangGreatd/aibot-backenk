package com.juzipi.springbootinit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AiConfig
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 16:08
 */
@Configuration
@ConfigurationProperties(prefix = "ai.anythingllm")
@Data
public class AiConfig {

    private String accessKey;

    private String workspaceId;

    private String apiAddress;

    private Integer temperature;
}
