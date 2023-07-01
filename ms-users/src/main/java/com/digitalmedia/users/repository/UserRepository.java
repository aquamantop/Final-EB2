package com.digitalmedia.users.repository;

import com.digitalmedia.users.model.User;

import java.util.List;

public interface UserRepository {

  User findById(String id);

  List<User> addUsers();

}