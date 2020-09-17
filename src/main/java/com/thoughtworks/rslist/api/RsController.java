package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.RsEventHandler;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.RsEventList;
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
import java.util.stream.Collectors;

@RestController
public class RsController {
  private static List<RsEvent> rsList = RsEventList.getRsEventList();
  List<User> userList =  UserList.getUserList();

  private List<String> getUserNameList(){
    return userList.stream().map(User::getName).collect(Collectors.toList());
  }

  @GetMapping("/rs/{index}")
  public ResponseEntity getOneRsEvent(@PathVariable int index){
    if (index<=0 || index>rsList.size()){
      throw new RsEventNotValidException("invalid index");
    }
    return ResponseEntity.ok(rsList.get(index-1));
  }

  @GetMapping("/rs/list")
  public ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    if (start == null || end == null){
      return ResponseEntity.ok(rsList);
    }
    if (start<=0 || start>rsList.size()||end<=0||end>rsList.size()||end<start){
      throw new RsEventNotValidException("invalid request param");
    }
    return ResponseEntity.ok(rsList.subList(start-1,end));
  }

  @PostMapping("/rs/event")
  public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent){
    rsList.add(rsEvent);
    User eventUser = rsEvent.getUser();
    if (!getUserNameList().contains(eventUser.getName())){
      userList.add(eventUser);
    }
    return ResponseEntity.created(URI.create(String.valueOf(rsList.size()))).build();
  }

  @PatchMapping("/rs/{index}")
  public ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent ,@PathVariable int index){
    RsEvent IndexRsEvent = rsList.get(index - 1);
    if (rsEvent.getEventName()!= null){
      IndexRsEvent.setEventName(rsEvent.getEventName());
    }
    if (rsEvent.getKeyWord()!= null){
      IndexRsEvent.setKeyWord(rsEvent.getKeyWord());
    }
    return ResponseEntity.created(URI.create(String.valueOf(index))).build();
  }

  @DeleteMapping("/rs/{index}")
  public ResponseEntity deleteRsEvent(@PathVariable int index){
    return ResponseEntity.ok(rsList.remove(index-1));
  }

  private static Logger LOGGER = LoggerFactory.getLogger(RsEventHandler.class);
  @ExceptionHandler({RsEventNotValidException.class, MethodArgumentNotValidException.class})
  public ResponseEntity rsExceptionHandler(Exception e){
    String errorMessage;
    if (e instanceof MethodArgumentNotValidException){
      errorMessage = "invalid param";
    } else {
      errorMessage = e.getMessage();
    }
    LOGGER.error("=======" + e.getMessage() + "=======");
    Error error = new Error();
    error.setError(errorMessage);
    return ResponseEntity.badRequest().body(error);
  }
}
