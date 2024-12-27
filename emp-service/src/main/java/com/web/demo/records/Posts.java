package com.web.demo.records;

public record Posts(
        int userId,
        int id,
        String title,
        String body
) {
}
