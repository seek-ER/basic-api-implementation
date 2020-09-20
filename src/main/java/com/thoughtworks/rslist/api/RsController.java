package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.*;
import com.thoughtworks.rslist.service.RsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RsController {

  @Autowired
  RsService rsService;

  @PostMapping("/rs/event")
  public ResponseEntity<Void> addRsEvent(@RequestBody @Validated(CreateAction.class) RsEvent rsEvent){
    final int addedId = rsService.addRsEvent(rsEvent);
    return ResponseEntity.created(null).header("added_Id",String.valueOf(addedId)).build();
  }

  @DeleteMapping("/rs/{id}")
  public ResponseEntity<Void> deleteRsEvent(@PathVariable int id){
    rsService.deleteRsEvent(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/rs/{id}")
  public ResponseEntity<ReturnRsEventData> getOneRsEvent(@PathVariable int id){
    final ReturnRsEventData oneRsEvent = rsService.getOneRsEvent(id);
    return ResponseEntity.ok(oneRsEvent);
  }

  @GetMapping("/rs/list")
  public ResponseEntity<List<ReturnRsEventData>> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    final List<ReturnRsEventData> rsEventBetween = rsService.getRsEventBetween(start, end);
    return ResponseEntity.ok(rsEventBetween);
  }

  @PatchMapping("/rs/{rsEventId}")
  public ResponseEntity<Void> modifyRsEvent(@RequestBody @Validated RsEvent rsEvent , @PathVariable int rsEventId){
    rsService.modifyRsEvent(rsEvent,rsEventId);
    return ResponseEntity.ok().header("modified_rs_event_id",String.valueOf(rsEventId)).build();
  }
}
