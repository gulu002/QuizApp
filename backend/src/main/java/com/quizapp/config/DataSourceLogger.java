package com.quizapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceLogger {

    private static final Logger log = LoggerFactory.getLogger(DataSourceLogger.class);

    @Bean
    CommandLineRunner logDataSource(DataSource dataSource, Environment env) {
        return args -> {
            String url = env.getProperty("spring.datasource.url");
            String username = env.getProperty("spring.datasource.username");
            String driver = env.getProperty("spring.datasource.driver-class-name");

            log.info("========== 数据库连接信息 ==========");
            log.info("驱动:     {}", driver);
            log.info("URL:      {}", url);
            log.info("用户名:   {}", username);
            log.info("----------------------------------");

            try (Connection conn = dataSource.getConnection()) {
                String dbUrl = conn.getMetaData().getURL();
                String dbProduct = conn.getMetaData().getDatabaseProductName();
                String dbVersion = conn.getMetaData().getDatabaseProductVersion();
                log.info("连接成功 → {} {} @ {}", dbProduct, dbVersion, dbUrl);
            } catch (Exception e) {
                log.error("连接失败: {}", e.getMessage(), e);
            }
            log.info("====================================");
        };
    }
}
