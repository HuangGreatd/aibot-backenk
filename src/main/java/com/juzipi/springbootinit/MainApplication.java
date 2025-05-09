package com.juzipi.springbootinit;

import cn.dev33.satoken.SaManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 */
// todo 如需开启 Redis，须移除 exclude 中的内容 exclude = {RedisAutoConfiguration.class}
@SpringBootApplication()
@MapperScan("com.juzipi.springbootinit.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(MainApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

}
