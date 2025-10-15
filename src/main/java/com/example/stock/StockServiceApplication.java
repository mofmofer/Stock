package com.example.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションを起動するエントリーポイントです。
 */
@SpringBootApplication
public class StockServiceApplication {

    /**
     * Spring Boot アプリケーションを起動します。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }
}
