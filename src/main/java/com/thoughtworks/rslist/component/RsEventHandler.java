package com.thoughtworks.rslist.component;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.exception.UserNotValidException;
import com.thoughtworks.rslist.exception.VoteNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

@ControllerAdvice
public class RsEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsEventHandler.class);

    @ExceptionHandler({RsEventNotValidException.class,UserNotValidException.class, VoteNotValidException.class})
    public ResponseEntity<Error> rsExceptionHandler(Exception e){
        String errorMessage= e.getMessage();
        LOGGER.error("=======" + e.getMessage() + "=======");
        Error error = new Error();
        error.setError(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> MethodArgumentNotValidHandler(Exception e, HandlerMethod handlerMethod){
        String errorMessage = null;
        final String methodName = handlerMethod.getMethod().getDeclaringClass().getName();
        if (methodName.equals("com.thoughtworks.rslist.api.UserController")){
            errorMessage = "invalid user";
        }
        if (methodName.equals("com.thoughtworks.rslist.api.RsController")){
            errorMessage = "invalid param";
        }
        LOGGER.error("=======" + e.getMessage() + "=======");
        Error error = new Error();
        error.setError(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
}
