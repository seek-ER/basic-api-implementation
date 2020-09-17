package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.RsEventHandler;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.UserList;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
