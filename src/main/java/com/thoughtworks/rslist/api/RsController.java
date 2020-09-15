package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RsController {
  private static List<RsEvent> rsList;

  private static List<RsEvent> initRsEventList() {
    List<RsEvent> rsEventList = new ArrayList<>();
    rsEventList.add(new RsEvent("第一条事件","无标签"));
    rsEventList.add(new RsEvent("第二条事件","无标签"));
    rsEventList.add(new RsEvent("第三条事件","无标签"));
    return rsEventList;
  }

  public static void reset(){
    rsList = initRsEventList();
  }

  @GetMapping("rs/{index}")
  public RsEvent getOneRsEvent(@PathVariable int index){
    return rsList.get(index-1);
  }

  @GetMapping("rs/list")
  public List<RsEvent> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    if (start == null || end == null){
      return rsList;
    }
    return rsList.subList(start-1,end);
  }

  @PostMapping("/rs/event")
  public void addRsEvent(@RequestBody RsEvent rsEvent){
    rsList.add(rsEvent);
  }

  @PatchMapping("rs/{index}")
  public void modifyRsEvent(@PathVariable int index, @RequestParam(required = false) String eventName, @RequestParam(required = false) String keyWord){
    RsEvent modifiedEvent;
    if (eventName==null||eventName.length()==0){
      modifiedEvent = new RsEvent();
      modifiedEvent.setKeyWord(keyWord);
    }else if (keyWord==null||keyWord.length()==0){
      modifiedEvent = new RsEvent();
      modifiedEvent.setEventName(eventName);
    }else {
      modifiedEvent = new RsEvent(eventName, keyWord);
    }
    rsList.set(index-1,modifiedEvent);
  }

  @DeleteMapping("rs/{index}")
  public RsEvent deleteRsEvent(@PathVariable int index){
    return rsList.remove(index-1);
  }
}
