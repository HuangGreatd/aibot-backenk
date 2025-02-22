package com.juzipi.springbootinit.controller;

import com.juzipi.springbootinit.manager.AIManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName AIController
 * @Description:
 * @Author: 橘子皮
 * @CreateDate: 2025/2/21 16:48
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {
    @Resource
    private AIManager aiManager;



}
