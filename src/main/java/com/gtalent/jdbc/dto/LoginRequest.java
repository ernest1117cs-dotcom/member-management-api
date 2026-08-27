package com.gtalent.jdbc.dto;

public record LoginRequest(
        String username,
        String password
) {
}
