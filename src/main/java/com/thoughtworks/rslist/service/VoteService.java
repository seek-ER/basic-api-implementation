package com.thoughtworks.rslist.service;


import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class VoteService {
    @Autowired
    VoteRepository voteRepository;

    public List<VotePO> getVoteRecord(int userId,int rsEventId,int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex - 1, 5);
        return voteRepository.findAllByUserPOIdAndRsEventPOId(userId, rsEventId, pageable);
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
