package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.RsEventHandler;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.UserList;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    List<User> userList = UserList.getUserList();

    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@RequestBody @Valid User user) {
        UserPO userPO = new UserPO();
        userPO.setUserName(user.getName());
        userPO.setGender(user.getGender());
        userPO.setAge(user.getAge());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNumber(user.getVoteNumber());
        userRepository.save(userPO);
        return ResponseEntity.created(null).header("index", String.valueOf(userPO.getId())).build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserPO> getUserById(@PathVariable int id){
        Optional<UserPO> userPO = userRepository.findById(id);
        if (!userPO.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Optional<UserPO> userPOById = userPO;
        return ResponseEntity.ok(userPOById.get());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable int id){
        if (!userRepository.findById(id).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/user")
    public ResponseEntity getUserList(){
        return ResponseEntity.ok(userList);
    }

    private static Logger LOGGER = LoggerFactory.getLogger(RsEventHandler.class);
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity UserHandler(Exception e){
        String errorMessage;
        if (e instanceof MethodArgumentNotValidException){
            errorMessage = "invalid user";
        } else {
            errorMessage = e.getMessage();
        }
        LOGGER.error("=======" + e.getMessage() + "=======");
        Error error = new Error();
        error.setError(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
}
