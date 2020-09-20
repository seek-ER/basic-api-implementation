package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.ReturnRsEventData;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsService {
    @Autowired
    RsEventRepository rsEventRepository;

    @Autowired
    UserRepository userRepository;

    public int addRsEvent(RsEvent rsEvent){
        Optional<UserPO> userPO = userRepository.findById(rsEvent.getUserId());
        if (!userPO.isPresent()){
            throw new RsEventNotValidException("invalid user id");
        }
        RsEventPO rsEventPO = RsEventPO.builder().keyWord(rsEvent.getKeyWord()).eventName(rsEvent.getEventName()).userPO(userPO.get()).build();
        rsEventRepository.save(rsEventPO);
        final int size = rsEventRepository.findAll().size();
        return rsEventRepository.findAll().get(size - 1).getId();
    }

    public void deleteRsEvent(int id){
        if (!rsEventRepository.existsById(id)){
            throw new RsEventNotValidException("invalid user id");
        }
        rsEventRepository.deleteById(id);
    }

    public ReturnRsEventData getOneRsEvent(int id){
        final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
        if (id<=0 || id>allRsEventPO.size()){
            throw new RsEventNotValidException("invalid id");
        }
        final RsEventPO rsEventPO = allRsEventPO.get(id - 1);
        return ReturnRsEventData.builder().eventName(rsEventPO.getEventName())
                .keyWord(rsEventPO.getKeyWord())
                .id(rsEventPO.getId())
                .voteNum(rsEventPO.getVoteNum()).build();
    }

    public List<ReturnRsEventData> getRsEventBetween(Integer start, Integer end){
        final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
        final List<ReturnRsEventData> returnRsEventData = allRsEventPO.stream().map(rsEventPO -> ReturnRsEventData.builder()
                .eventName(rsEventPO.getEventName())
                .keyWord(rsEventPO.getKeyWord())
                .id(rsEventPO.getId())
                .voteNum(rsEventPO.getVoteNum()).build()).collect(Collectors.toList());
        if (start == null || end == null){
            return returnRsEventData;
        }
        if (start<=0 || start>allRsEventPO.size()||end<=0||end>allRsEventPO.size()||end<start){
            throw new RsEventNotValidException("invalid request param");
        }
        return returnRsEventData.subList(start-1,end);
    }

    public void modifyRsEvent(RsEvent rsEvent , int rsEventId){
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
    }
}
