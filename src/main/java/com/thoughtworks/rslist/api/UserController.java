package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.RsEventHandler;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.UserNotValidException;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@RequestBody @Valid User user) {
        if (userRepository.existsByUserName(user.getName())) {
            throw new UserNotValidException("username has been used");
        }
        UserPO userPO = new UserPO();
        userPO.setUserName(user.getName());
        userPO.setGender(user.getGender());
        userPO.setAge(user.getAge());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNumber(user.getVoteNumber());
        userRepository.save(userPO);
        final int size = userRepository.findAll().size();
        final int addedUserId = userRepository.findAll().get(size - 1).getId();
        return ResponseEntity.created(null).header("added_user_id", String.valueOf(addedUserId)).build();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id){
        if (!userRepository.findById(id).isPresent()){
            throw new UserNotValidException("user id not valid");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserPO> getUserById(@PathVariable int id){
        Optional<UserPO> userPO = userRepository.findById(id);
        if (!userPO.isPresent()){
            throw new UserNotValidException("user id not valid");
        }
        return ResponseEntity.ok(userPO.get());
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserPO>> getUserList(){
        final List<UserPO> allUser = userRepository.findAll();
        return ResponseEntity.ok(allUser);
    }
}
