package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.service.RsService;
import com.thoughtworks.rslist.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class VoteController {
    @Autowired
    VoteService voteService;

    @Autowired
    RsService rsService;

    @PatchMapping("/rs/vote/{rsEventId}")
    public ResponseEntity<Void> vote(@PathVariable int rsEventId, @RequestBody Vote vote) {
        rsService.vote(rsEventId,vote);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/voteRecord")
    public ResponseEntity<List<VotePO>> getVoteRecord(@RequestParam int userId, @RequestParam int rsEventId, @RequestParam int pageIndex) {
        final List<VotePO> voteRecord = voteService.getVoteRecord(userId, rsEventId, pageIndex);
        return ResponseEntity.ok(voteRecord);
    }

    @GetMapping("/voteRecordByTime")
    public ResponseEntity<List<VotePO>> getVoteRecordByTime(@RequestParam String startTime,@RequestParam String endTime) {
        final List<VotePO> voteRecord = voteService.getVoteRecordByTime(startTime,endTime);
        return ResponseEntity.ok(voteRecord);
    }
}
