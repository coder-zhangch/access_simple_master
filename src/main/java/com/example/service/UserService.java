package com.example.service;

import com.example.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    Boolean save(User user);

    User getLastData();
}
