package com.digitalmedia.users.controller;

import com.digitalmedia.users.model.User;
import com.digitalmedia.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/get/{id}")
    public ResponseEntity<User> get(@PathVariable String id){
        return ResponseEntity.ok().body((service.findById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<List<User>> addUsers(){
        return ResponseEntity.ok().body(service.addUsers());
    }

}
