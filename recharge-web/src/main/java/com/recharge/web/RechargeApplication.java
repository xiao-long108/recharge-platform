package com.recharge.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 话费充值系统启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.recharge")
@MapperScan("com.recharge.mapper")
@EnableScheduling
public class RechargeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RechargeApplication.class, args);
        System.out.println("====================================");
        System.out.println("  话费充值系统启动成功!");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}
