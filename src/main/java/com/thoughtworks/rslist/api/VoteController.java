package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController {
    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    @PatchMapping("/rs/vote/{rsEventId}")
    public ResponseEntity vote(@PathVariable int rsEventId, @RequestBody Vote vote) {
        final int userId = vote.getUserId();
        if (!(userRepository.findById(userId).isPresent() || rsEventRepository.findById(rsEventId).isPresent())){
            return ResponseEntity.badRequest().build();
        }
        final UserPO userPO = userRepository.findById(userId).get();
        final RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
        final int voteNumberOfUser = userPO.getVoteNumber();
        final int voteNum = vote.getVoteNum();
        if (voteNumberOfUser<voteNum){
            return ResponseEntity.badRequest().build();
        }
        rsEventPO.setVoteNum(rsEventPO.getVoteNum()+voteNum);
        userPO.setVoteNumber(userPO.getVoteNumber()-voteNum);
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        final VotePO votePO = VotePO.builder().rsEventId(rsEventId).userId(vote.getUserId()).time(vote.getTime()).voteNum(voteNum).build();
        voteRepository.save(votePO);
        return ResponseEntity.ok().build();
    }
}
