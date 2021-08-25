package com.itheima.hchat;

import com.itheima.hchat.util.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/23 18:10
 * @version: v1.0
 */
@SpringBootApplication
@MapperScan(basePackages = "com.itheima.hchat.mapper")
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(0, 0);
    }

}
