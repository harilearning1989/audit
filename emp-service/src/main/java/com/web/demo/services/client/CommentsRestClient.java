package com.web.demo.services.client;

import com.web.demo.records.Comments;
import com.web.demo.records.Posts;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/comments")
public interface CommentsRestClient {

    @GetExchange
    List<Comments> getAllComments();
}
