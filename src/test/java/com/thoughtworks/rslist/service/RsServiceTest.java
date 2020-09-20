package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.TradeNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.TradeRecordPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRecordRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
    RsService rsService;

    @Mock
    RsEventRepository rsEventRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    VoteRepository voteRepository;
    @Mock
    TradeRecordRepository tradeRecordRepository;

    LocalDateTime localDateTime;
    Vote vote;
    Trade trade;

    @BeforeEach
    void setUp() {
        initMocks(this);
        rsService = new RsService(rsEventRepository, userRepository, voteRepository,tradeRecordRepository);
        localDateTime = LocalDateTime.now();
        vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
        trade = Trade.builder().rank(1).amount(100).build();
    }

    @Test
    void shouBuySuccess(){
        //given
        UserPO userPO =
                UserPO.builder()
                        .voteNumber(5)
                        .phone("18888888888")
                        .gender("female")
                        .email("a@b.com")
                        .age(19)
                        .userName("xiaoli")
                        .id(1)
                        .build();
        RsEventPO initialRsEventPO =
                RsEventPO.builder()
                        .eventName("event name before")
                        .id(1)
                        .keyWord("keyword")
                        .voteNum(2)
                        .userPO(userPO)
                        .rank(1)
                        .amount(50)
                        .build();
        RsEventPO changeRsEventPO =
                RsEventPO.builder()
                        .eventName("event name want to buy")
                        .id(2)
                        .keyWord("keyword")
                        .voteNum(2)
                        .userPO(userPO)
                        .build();
        when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(changeRsEventPO));
        when(rsEventRepository.findByRank(initialRsEventPO.getRank())).thenReturn(Optional.of(initialRsEventPO));
        //when
        rsService.buy(trade, changeRsEventPO.getId());
        //then
        verify(rsEventRepository).deleteByRank(initialRsEventPO.getRank());
        changeRsEventPO.setRank(trade.getRank());
        changeRsEventPO.setAmount(trade.getAmount());
        verify(rsEventRepository).save(changeRsEventPO);
        verify(tradeRecordRepository).save(
                TradeRecordPO.builder()
                        .rsEventPOId(changeRsEventPO.getId())
                        .amount(trade.getAmount())
                        .rank(trade.getRank())
                        .build());
    }

    @Test
    void shouBuyFailWhenAmountNotValid(){
        //given
        UserPO userPO =
                UserPO.builder()
                        .voteNumber(5)
                        .phone("18888888888")
                        .gender("female")
                        .email("a@b.com")
                        .age(19)
                        .userName("xiaoli")
                        .id(1)
                        .build();
        RsEventPO initialRsEventPO =
                RsEventPO.builder()
                        .eventName("event name before")
                        .id(1)
                        .keyWord("keyword")
                        .voteNum(2)
                        .userPO(userPO)
                        .rank(1)
                        .amount(150)
                        .build();
        RsEventPO changeRsEventPO =
                RsEventPO.builder()
                        .eventName("event name want to buy")
                        .id(2)
                        .keyWord("keyword")
                        .voteNum(2)
                        .userPO(userPO)
                        .build();
        when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(changeRsEventPO));
        when(rsEventRepository.findByRank(initialRsEventPO.getRank())).thenReturn(Optional.of(initialRsEventPO));
        //when&then
        assertThrows(
                TradeNotValidException.class,
                () -> {
                    rsService.buy(trade, changeRsEventPO.getId());
                });
    }

    @Test
    void shouldVoteSuccess() {
        // given
        UserPO userPO =
                UserPO.builder()
                        .voteNumber(5)
                        .phone("18888888888")
                        .gender("female")
                        .email("a@b.com")
                        .age(19)
                        .userName("xiaoli")
                        .id(1)
                        .build();
        RsEventPO rsEventPO =
                RsEventPO.builder()
                        .eventName("event name")
                        .id(1)
                        .keyWord("keyword")
                        .voteNum(2)
                        .userPO(userPO)
                        .build();

        when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventPO));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userPO));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(rsEventRepository.existsById(anyInt())).thenReturn(true);
        // when
        rsService.vote(1, vote);
        // then
        verify(voteRepository)
                .save(
                        VotePO.builder()
                                .voteNum(2)
                                .time(localDateTime)
                                .userPO(userPO)
                                .rsEventPO(rsEventPO)
                                .build());
        verify(userRepository).save(userPO);
        verify(rsEventRepository).save(rsEventPO);
    }

    @Test
    void shouldThrowExceptionWhenUserNotExist() {
        // given
        when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when&then
        assertThrows(
                RuntimeException.class,
                () -> {
                    rsService.vote(1, vote);
                });
    }
}