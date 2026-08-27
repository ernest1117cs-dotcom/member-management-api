package com.gtalent.jdbc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MemberRequest {

    @NotBlank(message = "姓名不能為空白")
    private String name;

    @NotBlank(message = "Email 不能為空白")
    @Email(message = "Email 格式不正確")
    private String email;

    @Min(value = 1, message = "年齡不能小於 1")
    @Max(value = 120, message = "年齡不能大於 120")
    private int age;

    public MemberRequest() {
    }

    public MemberRequest(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
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
}