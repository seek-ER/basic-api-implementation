package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.UserList;
import com.thoughtworks.rslist.service.RsDataHelper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RsController {
  private static List<RsEvent> rsList = new RsDataHelper().provideInitialRsEventList();
  public static List<RsEvent> getRsList() {
    return rsList;
  }
  List<User> userList =  UserList.getUserList();

  private List<String> getUserNameList(){
    return userList.stream().map(user -> user.getName()).collect(Collectors.toList());
  }

  @GetMapping("/rs/{index}")
  public RsEvent getOneRsEvent(@PathVariable int index){
    return rsList.get(index-1);
  }

  @GetMapping("/rs/list")
  public List<RsEvent> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    if (start == null || end == null){
      return rsList;
    }
    return rsList.subList(start-1,end);
  }

  @PostMapping("/rs/event")
  public void addRsEvent(@RequestBody @Valid RsEvent rsEvent){
    rsList.add(rsEvent);
    User eventUser = rsEvent.getUser();
    if (!getUserNameList().contains(eventUser.getName())){
      userList.add(eventUser);
    }
  }

  @PatchMapping("/rs/{index}")
  public void modifyRsEvent(@RequestBody RsEvent rsEvent ,@PathVariable int index){
    RsEvent IndexRsEvent = rsList.get(index - 1);
    if (rsEvent.getEventName()!= null){
      IndexRsEvent.setEventName(rsEvent.getEventName());
    }
    if (rsEvent.getKeyWord()!= null){
      IndexRsEvent.setKeyWord(rsEvent.getKeyWord());
    }
  }

  @DeleteMapping("/rs/{index}")
  public RsEvent deleteRsEvent(@PathVariable int index){
    return rsList.remove(index-1);
  }
}
