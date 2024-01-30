package com.linyi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author zhongzb
 * @date 2021/05/27
 */
@SpringBootApplication(scanBasePackages = {"com.linyi"})
@MapperScan({"com.linyi.**.mapper"})
@ServletComponentScan
public class DiTingCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiTingCustomApplication.class,args);
    }

}