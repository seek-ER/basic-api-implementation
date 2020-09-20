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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RsController {

  @Autowired
  RsEventRepository rsEventRepository;

  @Autowired
  UserRepository userRepository;

  @PostMapping("/rs/event")
  public ResponseEntity<Void> addRsEvent(@RequestBody @Validated(CreateAction.class) RsEvent rsEvent){
    Optional<UserPO> userPO = userRepository.findById(rsEvent.getUserId());
    if (!userPO.isPresent()){
      throw new RsEventNotValidException("invalid user id");
    }
    RsEventPO rsEventPO = RsEventPO.builder().keyWord(rsEvent.getKeyWord()).eventName(rsEvent.getEventName()).userPO(userPO.get()).build();
    rsEventRepository.save(rsEventPO);
    return ResponseEntity.created(null).header("added_Id",String.valueOf(rsEventRepository.findAll().get(0).getId())).build();
  }

  @DeleteMapping("/rs/{id}")
  public ResponseEntity<Void> deleteRsEvent(@PathVariable int id){
    if (!rsEventRepository.existsById(id)){
      throw new RsEventNotValidException("invalid user id");
    }
    rsEventRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/rs/{id}")
  public ResponseEntity<ReturnRsEventData> getOneRsEvent(@PathVariable int id){
    final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
    if (id<=0 || id>allRsEventPO.size()){
      throw new RsEventNotValidException("invalid id");
    }
    final RsEventPO rsEventPO = allRsEventPO.get(id - 1);
    ReturnRsEventData returnRsEventData = ReturnRsEventData.builder().eventName(rsEventPO.getEventName())
            .keyWord(rsEventPO.getKeyWord())
            .id(rsEventPO.getId())
            .voteNum(rsEventPO.getVoteNum()).build();
    return ResponseEntity.ok(returnRsEventData);
  }

  @GetMapping("/rs/list")
  public ResponseEntity<List<ReturnRsEventData>> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
    final List<ReturnRsEventData> returnRsEventData = allRsEventPO.stream().map(rsEventPO -> ReturnRsEventData.builder()
            .eventName(rsEventPO.getEventName())
            .keyWord(rsEventPO.getKeyWord())
            .id(rsEventPO.getId())
            .voteNum(rsEventPO.getVoteNum()).build()).collect(Collectors.toList());
    if (start == null || end == null){
      return ResponseEntity.ok(returnRsEventData);
    }
    if (start<=0 || start>allRsEventPO.size()||end<=0||end>allRsEventPO.size()||end<start){
      throw new RsEventNotValidException("invalid request param");
    }
    return ResponseEntity.ok(returnRsEventData.subList(start-1,end));
  }

  @PatchMapping("/rs/{rsEventId}")
  public ResponseEntity<Void> modifyRsEvent(@RequestBody @Validated RsEvent rsEvent , @PathVariable int rsEventId){
    int modifiedUserId = rsEvent.getUserId();
    if (!rsEventRepository.existsById(rsEventId)){
      throw new RsEventNotValidException("rsEvent id do not exist");
    }
    RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
    int rsEventCorrespondingUserId = rsEventPO.getUserPO().getId();
    if (modifiedUserId == rsEventCorrespondingUserId){
      if (rsEvent.getEventName()!= null){
        rsEventPO.setEventName(rsEvent.getEventName());
      }
      if (rsEvent.getKeyWord()!= null){
        rsEventPO.setKeyWord(rsEvent.getKeyWord());
      }
      rsEventRepository.save(rsEventPO);
    }else {
      throw new RsEventNotValidException("user id not match");
    }
    return ResponseEntity.ok().header("modified_rs_event_id",String.valueOf(rsEventId)).build();
  }

  private static Logger LOGGER = LoggerFactory.getLogger(RsEventHandler.class);
  @ExceptionHandler({RsEventNotValidException.class, MethodArgumentNotValidException.class})
  public ResponseEntity<Error> rsExceptionHandler(Exception e){
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
