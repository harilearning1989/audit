package com.web.demo.services.client;

import com.web.demo.records.Todos;
import com.web.demo.records.Users;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/todos")
public interface TodosRestClient {

    @GetExchange
    List<Todos> getAllTodos();
}
