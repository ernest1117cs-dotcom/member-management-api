package com.gtalent.jdbc.model;

import java.time.LocalDateTime;

/**
 * Member 模型（對應資料表：member）
 *
 * 欄位：
 * - id: 主鍵，自增
 * - name: 會員姓名
 * - email: 會員電子郵件 (唯一)
 * - age: 年齡
 */
public class Member {
    private int id;
    private String name;
    private String email;
    private int age;
    private String status;
    private LocalDateTime createdAt;

    // 無參數建構子：BeanPropertyRowMapper 建立物件時需要使用。
    public Member() {
    }

    // 全參建構子：可在測試或手動建立完整會員資料時使用。
    public Member(int id, String name, String email, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // 不含 id 的建構子：新增資料時通常不需要提供 id，因為資料庫會自動生成。
    public Member(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
