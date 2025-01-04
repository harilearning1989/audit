package com.web.demo.records;

public record Todos(
        int userId,
        int id,
        String title,
        boolean completed
) {
}
