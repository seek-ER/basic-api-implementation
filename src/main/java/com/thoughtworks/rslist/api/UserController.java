package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@RequestBody @Valid User user) {
        final int addedUserId = userService.addUser(user);
        return ResponseEntity.created(null).header("added_user_id", String.valueOf(addedUserId)).build();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id){
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserPO> getUserById(@PathVariable int id){
        final UserPO userPO = userService.getUserById(id);
        return ResponseEntity.ok(userPO);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserPO>> getUserList(){
        final List<UserPO> userList = userService.getUserList();
        return ResponseEntity.ok(userList);
    }
}
