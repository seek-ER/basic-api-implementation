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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


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
        final VotePO votePO = VotePO.builder().rsEventPO(rsEventPO).userPO(userPO).time(vote.getTime()).voteNum(voteNum).build();
        voteRepository.save(votePO);
    }

    public List<VotePO> getVoteRecord(int userId,int rsEventId,int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex - 1, 5);
        return voteRepository.findAllByUserPOIdAndRsEventPOId(userId, rsEventId, pageable);
/*                .stream()
                        .map(
                                item ->
                                        VotePO.builder()
                                                .voteNum(item.getVoteNum())
                                                .userPO(item.getUserPO())
                                                .time(item.getTime())
                                                .rsEventPO(item.getRsEventPO())
                                                .build())
                        .collect(Collectors.toList());*/
    }

    public List<VotePO> getVoteRecordByTime(String startTimeStr, String endTimeStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr,df);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr,df);
        return voteRepository.findAll().stream()
                .filter(item -> !(item.getTime().isBefore(startTime)))
                .filter(item -> !(item.getTime().isAfter(endTime)))
                .collect(Collectors.toList());
    }
}
