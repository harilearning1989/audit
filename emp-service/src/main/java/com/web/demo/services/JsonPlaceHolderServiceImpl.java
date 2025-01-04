package com.web.demo.services;

import com.web.demo.records.Comments;
import com.web.demo.records.Posts;
import com.web.demo.records.Todos;
import com.web.demo.records.Users;
import com.web.demo.services.client.CommentsRestClient;
import com.web.demo.services.client.JsonPlaceHolderClient;
import com.web.demo.services.client.TodosRestClient;
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

    @Autowired
    private CommentsRestClient commentsRestClient;

    @Autowired
    private TodosRestClient todosRestClient;

    @Override
    public List<Posts> getAllPosts() {
        List<Users> usersList = usersRestClient.getAllUsers();
        List<Comments> commentsList = commentsRestClient.getAllComments();
        List<Todos> todosList = todosRestClient.getAllTodos();
        return jsonplaceholderClient.getAllPosts();
    }
}
