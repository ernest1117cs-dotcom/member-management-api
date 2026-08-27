package com.gtalent.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 應用程式啟動類別 (Spring Boot)
 *
 * 說明：
 * - 程式進入點，透過 SpringApplication.run 啟動 Spring Boot 應用。
 * - 本專案使用內建 H2 記憶體資料庫，啟動後可至 /h2-console 檢視資料。
 */
@EnableCaching
@SpringBootApplication
public class JdbcApplication {

	// main 是 Java 應用程式進入點，會啟動 Spring 容器與內嵌伺服器。
	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

}
