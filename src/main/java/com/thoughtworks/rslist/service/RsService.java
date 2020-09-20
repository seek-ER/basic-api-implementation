package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.ReturnRsEventData;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.exception.TradeNotValidException;
import com.thoughtworks.rslist.exception.VoteNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.TradeRecordPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRecordRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsService {
    RsEventRepository rsEventRepository;
    UserRepository userRepository;
    VoteRepository voteRepository;
    TradeRecordRepository tradeRecordRepository;

    public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository,TradeRecordRepository tradeRecordRepository ) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.tradeRecordRepository = tradeRecordRepository;
    }

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
        rank();
        final List<RsEventPO> allRsEventPO = rsEventRepository.findAllSortedByRank();
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
        rank();
    }

    @Modifying
    @Transactional
    public void buy(Trade trade, int id) {
        final int rank = trade.getRank();
        final int amount = trade.getAmount();
        final Optional<RsEventPO> rsEventPOOptional = rsEventRepository.findById(id);
        if (!rsEventPOOptional.isPresent()){
            throw new RsEventNotValidException("invalid rsEvent id");
        }
        final RsEventPO rsEventPO = rsEventPOOptional.get();
        final Optional<RsEventPO> rsEventPOOptionalByRank = rsEventRepository.findByRank(rank);
        if (rsEventPOOptionalByRank.isPresent()) {
            if (rsEventPOOptionalByRank.get().getAmount() >= trade.getAmount()){
                throw new TradeNotValidException("invalid amount"); //400
            }else {
                rsEventRepository.deleteByRank(rank);
                rsEventPO.setAmount(amount);
                rsEventPO.setRank(rank);
                rsEventRepository.save(rsEventPO);
            }
        }else {
            rsEventPO.setAmount(amount);
            rsEventPO.setRank(rank);
            rsEventRepository.save(rsEventPO);
        }

        tradeRecordRepository.save(
                TradeRecordPO.builder()
                        .rsEventPOId(id)
                        .amount(amount)
                        .rank(rank)
                        .build()
        );
        rank();
    }

    public void rank(){
        final List<RsEventPO> sortNotBuyRsEvent = rsEventRepository.sortNotBuyRsEvent();
        final List<RsEventPO> buyRsEvent = rsEventRepository.findBuyRsEvent();
        final List<Integer> existRank = buyRsEvent.stream().map(item -> item.getRank()).collect(Collectors.toList());
        final int size = rsEventRepository.findAll().size();
        for (int i = 0,j=0; j < sortNotBuyRsEvent.size() && i<size; i++) {
            if (existRank.contains(i+1)) {
                continue;
            }
            final RsEventPO rsEventPO = sortNotBuyRsEvent.get(j);
            j++;
            rsEventPO.setRank(i+1);
            rsEventRepository.save(rsEventPO);
        }
    }
}
