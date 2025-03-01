package com.juzipi.springbootinit.common;

import com.juzipi.springbootinit.config.ForbiddenWordsProperties;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ForbiddenWordsDetector
 * @Description:违禁词检测器（AC自动机）
 * @Author: 橘子皮
 * @CreateDate: 2025/3/1 17:30
 */
@Slf4j
@Component
public class ForbiddenWordsDetector {
    private volatile Trie currentTrie;
    private final Set<String> forbiddenWords = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 通过构造器注入初始词库
    @Autowired
    public ForbiddenWordsDetector(ForbiddenWordsProperties properties) {
        Set<String> initialWords = properties.getInitialWords();
        if (initialWords != null && !initialWords.isEmpty()) {
            forbiddenWords.addAll(initialWords);
        }
        this.currentTrie = rebuildTrie();
        log.info("违禁词库初始化完成，数量：{}", forbiddenWords.size());
    }

    /**
     * 动态添加新违禁词（线程安全）
     * @param newWords 新增违禁词集合
     */
    public void addForbiddenWords(Set<String> newWords) {
        if (newWords == null || newWords.isEmpty()) return;

        boolean hasNewWord = false;
        for (String word : newWords) {
            // 仅当词库中不存在时添加
            if (forbiddenWords.add(word.trim())) {
                hasNewWord = true;
            }
        }
        if (hasNewWord) {
            currentTrie = rebuildTrie(); // 增量重建
        }
    }

    /**
     * 重建 AC自动机（线程安全隔离）
     */
    private synchronized Trie rebuildTrie() {
        // 创建隔离的 TrieBuilder 防止并发冲突
        Trie.TrieBuilder builder = Trie.builder();
        forbiddenWords.forEach(builder::addKeyword);
        return builder.build();
    }

    /**
     * 违禁词检测入口
     */
    public boolean containsForbiddenWords(String text) {
        return !currentTrie.parseText(text).isEmpty();
    }
}
