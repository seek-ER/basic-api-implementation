package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.service.RsDataHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RsController {
  private static List<RsEvent> rsList = new RsDataHelper().provideInitialRsEventList();
  public static List<RsEvent> getRsList() {
    return rsList;
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
  public void addRsEvent(@RequestBody RsEvent rsEvent){
    rsList.add(rsEvent);
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
