package com.web.demo.services;

import com.web.demo.records.Posts;
import com.web.demo.records.Users;
import com.web.demo.services.client.JsonPlaceHolderClient;
import com.web.demo.services.client.UsersRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JsonPlaceHolderServiceImpl implements JsonPlaceHolderService {

    @Autowired
    private JsonPlaceHolderClient jsonplaceholderClient;

    @Autowired
    private UsersRestClient usersRestClient;

    @Override
    public List<Posts> getAllPosts() {
        List<Users> usersList = usersRestClient.getAllUsers();
        return jsonplaceholderClient.getAllPosts();
    }
}
