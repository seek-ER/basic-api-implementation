package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    UserPO userPO;
    RsEventPO rsEventPO;
    LocalDateTime now;
    ObjectMapper objectMapper;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @BeforeEach
    void beforeEach(){
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();

        userPO = UserPO.builder().userName("kong").gender("male").age(22).email("a@b.com").phone("18882784492").voteNumber(10).build();
        userRepository.save(userPO);
        rsEventPO = RsEventPO.builder().eventName("eventName1").keyWord("keyWord1").voteNum(0).userPO(userPO).build();
        rsEventRepository.save(rsEventPO);

        now = LocalDateTime.now();
        String nowStr = df.format(now);
        now = LocalDateTime.parse(nowStr,df);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void afterEach(){
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
    }

    @Test
    public void should_be_vote_successfully() throws Exception {
        Vote vote = Vote.builder().rsEventId(rsEventPO.getId()).voteNum(5).userId(userPO.getId()).time(now).build();
        String jsonString = objectMapper.writeValueAsString(vote);

        int beforeRsEventVoteNum = rsEventRepository.findById(vote.getRsEventId()).get().getVoteNum();
        final int beforeUsrVoteNumber = userRepository.findById(vote.getUserId()).get().getVoteNumber();

        mockMvc.perform(patch("/rs/vote/{rsEventId}",vote.getRsEventId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(beforeRsEventVoteNum+vote.getVoteNum(),rsEventRepository.findById(vote.getRsEventId()).get().getVoteNum());
        assertEquals(beforeUsrVoteNumber-vote.getVoteNum(),userRepository.findById(vote.getUserId()).get().getVoteNumber());
    }

    @Test
    public void should_not_be_vote_if_vote_number_invalid() throws Exception {
        Vote vote = Vote.builder().rsEventId(rsEventPO.getId()).voteNum(15).userId(userPO.getId()).time(now).build();
        String jsonString = objectMapper.writeValueAsString(vote);

        mockMvc.perform(patch("/rs/vote/{rsEventId}",vote.getRsEventId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid voteNumber")));
    }

    @Test
    public void should_get_vote_records() throws Exception{
        for (int i = 0; i < 8; i++) {
            VotePO votePO = VotePO.builder().userPO(userPO).rsEventPO(rsEventPO).time(now)
                    .voteNum(i+1).build();
            voteRepository.save(votePO);
        }

        mockMvc.perform(get("/voteRecord").param("userId",String.valueOf(userPO.getId()))
                .param("rsEventId",String.valueOf(rsEventPO.getId())).param("pageIndex", "1"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print())
                .andExpect(jsonPath("$[0].userPO.id",is(userPO.getId())))
                .andExpect(jsonPath("$[0].rsEventPO.id",is(rsEventPO.getId())))
                .andExpect(jsonPath("$[0].voteNum",is(1)))
                .andExpect(jsonPath("$[1].voteNum",is(2)))
                .andExpect(jsonPath("$[2].voteNum",is(3)))
                .andExpect(jsonPath("$[3].voteNum",is(4)))
                .andExpect(jsonPath("$[4].voteNum",is(5)));

        mockMvc.perform(get("/voteRecord").param("userId",String.valueOf(userPO.getId()))
                .param("rsEventId",String.valueOf(rsEventPO.getId())).param("pageIndex", "2"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userPO.id",is(userPO.getId())))
                .andExpect(jsonPath("$[0].rsEventPO.id",is(rsEventPO.getId())))
                .andExpect(jsonPath("$[0].voteNum",is(6)))
                .andExpect(jsonPath("$[1].voteNum",is(7)))
                .andExpect(jsonPath("$[2].voteNum",is(8)));
    }

    @Test
    public void should_get_vote_records_by_time() throws Exception{
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        for (int i = 0; i < 8; i++) {
            now = LocalDateTime.now();
            String nowStr = df.format(now);
            now = LocalDateTime.parse(nowStr,df);
            if (i == 2){
                startTime = now;
            }
            if (i==7){
                endTime =now;
            }
            VotePO votePO = VotePO.builder().userPO(userPO).rsEventPO(rsEventPO).time(now)
                    .voteNum(i+1).build();
            voteRepository.save(votePO);
        }
        String startTimeStr = df.format(startTime);
        String endTimeStr = df.format(endTime);
        mockMvc.perform(get("/voteRecordByTime")
                .param("startTime", startTimeStr)
                .param("endTime", endTimeStr))
                .andExpect(jsonPath("$", hasSize(6)))
                .andDo(print());
    }
}