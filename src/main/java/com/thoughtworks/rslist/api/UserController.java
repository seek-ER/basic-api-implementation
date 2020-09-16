package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.UserList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserController {
    List<User> userList = UserList.getUserList();

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        userList.add(user);
        return ResponseEntity.created(URI.create(String.valueOf(userList.size()))).build();
    }

    @GetMapping("/user")
    public ResponseEntity getUserList(){
        return ResponseEntity.ok(userList);
    }
}
