package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteControllerTest {
    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();

        UserPO userPO = UserPO.builder().userName("kong").gender("male").age(22).email("a@b.com").phone("18882784492").voteNumber(10).build();
        userRepository.save(userPO);
        RsEventPO rsEventPO = RsEventPO.builder().eventName("eventName1").keyWord("keyWord1").voteNum(0).userPO(userPO).build();
        rsEventRepository.save(rsEventPO);
    }

    @Test
    public void should_be_vote_successfully() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String time = now.format(formatter);

        Vote vote = Vote.builder().rsEventId(1).voteNum(5).userId(1).time(time).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(vote);

        int beforeRsEventVoteNum = rsEventRepository.findById(vote.getRsEventId()).get().getVoteNum();
        final int beforeUsrVoteNumber = userRepository.findById(vote.getUserId()).get().getVoteNumber();

        mockMvc.perform(patch("/rs/vote/{rsEventId}",vote.getRsEventId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(beforeRsEventVoteNum+vote.getVoteNum(),rsEventRepository.findById(vote.getRsEventId()).get().getVoteNum());
        assertEquals(beforeUsrVoteNumber-vote.getVoteNum(),userRepository.findById(vote.getUserId()).get().getVoteNumber());
    }
}