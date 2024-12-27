package com.web.demo.services;

import com.web.demo.records.Posts;
import com.web.demo.services.client.JsonPlaceHolderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JsonPlaceHolderServiceImpl implements JsonPlaceHolderService {

    @Autowired
    private JsonPlaceHolderClient jsonplaceholderClient;

    @Override
    public List<Posts> getAllPosts() {
        return jsonplaceholderClient.getAllPosts();
    }
}
