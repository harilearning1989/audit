package com.web.demo.records;

public record Comments(
        int postId,
        int id,
        String name,
        String email,
        String body
) {
}
