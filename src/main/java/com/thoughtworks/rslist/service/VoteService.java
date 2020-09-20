package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.VoteNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VoteService {
    @Autowired
    RsEventRepository rsEventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    public void vote(int rsEventId, Vote vote) {
        final int userId = vote.getUserId();
        if (!userRepository.existsById(userId)){
            throw new VoteNotValidException("user is not exist.");
        }
        if (!rsEventRepository.existsById(rsEventId)){
            throw new VoteNotValidException("rsEvent is not exist.");
        }
        final UserPO userPO = userRepository.findById(userId).get();
        final RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
        final int voteNumberOfUser = userPO.getVoteNumber();
        final int voteNum = vote.getVoteNum();
        if (voteNumberOfUser<voteNum){
            throw new VoteNotValidException("invalid voteNumber");
        }
        rsEventPO.setVoteNum(rsEventPO.getVoteNum()+voteNum);
        userPO.setVoteNumber(userPO.getVoteNumber()-voteNum);
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        final VotePO votePO = VotePO.builder().rsEventId(rsEventId).userId(vote.getUserId()).time(vote.getTime()).voteNum(voteNum).build();
        voteRepository.save(votePO);
    }
}
