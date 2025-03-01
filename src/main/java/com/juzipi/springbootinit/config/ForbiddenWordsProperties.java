package com.juzipi.springbootinit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName ForbiddenWordsProperties
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/3/1 17:48
 */
@Component
@ConfigurationProperties(prefix = "forbidden")
public class ForbiddenWordsProperties {
    private Set<String> initialWords = new HashSet<>();

    public Set<String> getInitialWords() {
        return initialWords;
    }

    public void setInitialWords(Set<String> initialWords) {
        this.initialWords = initialWords;
    }
}