package com.gtalent.jdbc.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long id) {
        super("找不到 ID 為 " + id + " 的會員");
    }
}
