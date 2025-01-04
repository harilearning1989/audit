package com.web.demo.services.client;

import com.web.demo.records.Users;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/users")
public interface UsersRestClient {

    @GetExchange
    List<Users> getAllUsers();
}
