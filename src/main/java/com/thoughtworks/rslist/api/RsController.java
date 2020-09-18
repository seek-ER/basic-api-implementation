package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.RsEventHandler;
import com.thoughtworks.rslist.domain.*;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RsController {
  private static List<RsEvent> rsList = RsEventList.getRsEventList();
  List<User> userList =  UserList.getUserList();

  private List<String> getUserNameList(){
    return userList.stream().map(User::getName).collect(Collectors.toList());
  }

  @Autowired
  RsEventRepository rsEventRepository;

  @Autowired
  UserRepository userRepository;

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
    Optional<UserPO> userPO = userRepository.findById(rsEvent.getUserId());
    if (!userPO.isPresent()){
      return ResponseEntity.badRequest().build();
    }
    RsEventPO rsEventPO = RsEventPO.builder().keyWord(rsEvent.getKeyWord()).eventName(rsEvent.getEventName()).userPO(userPO.get()).build();
    rsEventRepository.save(rsEventPO);
    return ResponseEntity.created(null).header("index",String.valueOf(rsEventRepository.findAll().size())).build();
  }

  @PatchMapping("/rs/{rsEventId}")
  public ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent , @PathVariable int rsEventId){
    RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
    int modifiedUserId = rsEvent.getUserId();
    int rsEventCorrespondingId = rsEventPO.getUserPO().getId();
    if (modifiedUserId == rsEventCorrespondingId){
      if (rsEvent.getEventName()!= null){
        rsEventPO.setEventName(rsEvent.getEventName());
      }
      if (rsEvent.getKeyWord()!= null){
        rsEventPO.setKeyWord(rsEvent.getKeyWord());
      }
      rsEventRepository.save(rsEventPO);
    }else {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.created(null).header("index",String.valueOf(rsEventId)).build();
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
