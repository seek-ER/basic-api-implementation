package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thoughtworks.rslist.domain.*;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import jdk.nashorn.internal.ir.annotations.Ignore;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    @Autowired
    VoteRepository voteRepository;

    UserPO userPO;
    RsEventPO rsEventPO;
    LocalDateTime now;
    ObjectMapper objectMapper;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");


    @BeforeEach
    public void beforeEach(){
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        userPO = UserPO.builder().userName("kong").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        rsEventPO = RsEventPO.builder().eventName("eventName1").keyWord("keyWord1").userPO(userPO).build();

        now = LocalDateTime.now();
        String nowStr = df.format(now);
        now = LocalDateTime.parse(nowStr,df);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    public void afterEach(){
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
    }

    @Test
    public void should_add_rs_event_when_user_exist() throws Exception{
        userRepository.save(userPO);

        int savedUserPOId = userRepository.findAll().get(0).getId();

        RsEvent rsEvent = RsEvent.builder().eventName("猪肉涨价了").keyWord("经济").userId(savedUserPOId).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc
                .perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<RsEventPO> all = rsEventRepository.findAll();
        assertNotNull(all);
        assertEquals("猪肉涨价了",all.get(0).getEventName());
        assertEquals("经济",all.get(0).getKeyWord());
        assertEquals(userPO,all.get(0).getUserPO());
    }

    @Test
    public void should_not_add_rs_event_when_user_not_exist() throws Exception{
        RsEvent rsEvent = RsEvent.builder().eventName("猪肉涨价了").keyWord("经济").userId(100).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc
                .perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user id")));
    }

    @Test
    public void User_should_not_be_null() throws Exception{
        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("经济大爆炸");
        rsEvent.setKeyWord("经济");
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user id")));
    }

    @Test
    public void eventName_should_not_be_null() throws Exception{
        RsEvent rsEvent = new RsEvent();
        rsEvent.setKeyWord("经济");
        rsEvent.setUserId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    public void keyWord_should_not_be_null() throws Exception{
        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("经济");
        rsEvent.setUserId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    public void should_delete_rs_event() throws Exception{
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(1)));

        final int id = rsEventRepository.findAll().get(0).getId();
        mockMvc.perform(delete("/rs/{id}",id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    public void should_get_rs_event_list() throws Exception {
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).build();
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        ObjectMapper objectMapper = new ObjectMapper();
        final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
        final List<ReturnRsEventData> returnRsEventData = allRsEventPO.stream().map(rsEventPO -> ReturnRsEventData.builder()
                .eventName(rsEventPO.getEventName())
                .keyWord(rsEventPO.getKeyWord())
                .id(rsEventPO.getId())
                .voteNum(rsEventPO.getVoteNum()).build()).collect(Collectors.toList());
        String jsonString = objectMapper.writeValueAsString(returnRsEventData);

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_one_rs_event() throws Exception {
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        final RsEventPO rsEventPO = rsEventRepository.findAll().get(0);
        ReturnRsEventData returnRsEventData = ReturnRsEventData.builder().eventName(rsEventPO.getEventName())
                .keyWord(rsEventPO.getKeyWord())
                .id(rsEventPO.getId())
                .voteNum(rsEventPO.getVoteNum()).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(returnRsEventData);
        mockMvc.perform(get("/rs/1"))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_rs_event_between() throws Exception {
        UserPO userPO2 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).build();
        RsEventPO rsEventPO3 = RsEventPO.builder().eventName("eventName3").keyWord("keyWord3").userPO(userPO2).build();
        userRepository.save(userPO);
        userRepository.save(userPO2);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        rsEventRepository.save(rsEventPO3);
        ObjectMapper objectMapper = new ObjectMapper();
        final List<RsEventPO> allRsEventPO = rsEventRepository.findAll();
        final List<ReturnRsEventData> returnRsEventData = allRsEventPO.stream().map(rsEventPO -> ReturnRsEventData.builder()
                .eventName(rsEventPO.getEventName())
                .keyWord(rsEventPO.getKeyWord())
                .id(rsEventPO.getId())
                .voteNum(rsEventPO.getVoteNum()).build()).collect(Collectors.toList());
        String jsonString = objectMapper.writeValueAsString(returnRsEventData.subList(0,2));

        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_throw_rsEvent_not_valid_exception() throws Exception{
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid id")));
    }

    @Test
    public void should_throw_invalid_request_param_exception() throws Exception {
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=2&end=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }

    @Test
    public void should_modify_rs_event() throws Exception{
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);

        RsEvent rsEvent = new RsEvent("天上掉下个猪八戒", "奇闻",userRepository.findAll().get(0).getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}/",rsEventRepository.findAll().get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(rsEventRepository.findAll().get(0).getEventName(),rsEvent.getEventName());
        assertEquals(rsEventRepository.findAll().get(0).getKeyWord(),rsEvent.getKeyWord());
    }

    @Test
    public void should_modify_rs_event_only_keyword() throws Exception{
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);

        RsEvent rsEvent = new RsEvent();
        rsEvent.setKeyWord("奇闻");
        rsEvent.setUserId(userRepository.findAll().get(0).getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}/",rsEventRepository.findAll().get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(rsEventRepository.findAll().get(0).getEventName(),rsEventPO.getEventName());
        assertEquals(rsEventRepository.findAll().get(0).getKeyWord(),rsEvent.getKeyWord());
    }

    @Test
    public void should_modify_rs_event_only_event_name() throws Exception{
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);

        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("天上掉下个猪八戒");
        rsEvent.setUserId(userRepository.findAll().get(0).getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}/",rsEventRepository.findAll().get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(rsEventRepository.findAll().get(0).getEventName(),rsEvent.getEventName());
        assertEquals(rsEventRepository.findAll().get(0).getKeyWord(),rsEventPO.getKeyWord());
    }

    @Test
    public void should_not_modify_rs_event_not_user_id() throws Exception{
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);

        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("天上掉下个猪八戒");
        rsEvent.setKeyWord("奇闻");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}/",rsEventRepository.findAll().get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("user id not match")));
    }

    @Test
    public void should_show_by_rank_and_vote_number() throws Exception{
        UserPO userPO2 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).voteNum(2).build();
        RsEventPO rsEventPO3 = RsEventPO.builder().eventName("eventName3").keyWord("keyWord3").userPO(userPO2).rank(1).amount(100).build();
        userRepository.save(userPO);
        userRepository.save(userPO2);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        rsEventRepository.save(rsEventPO3);

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].eventName",is("eventName3")))
                .andExpect(jsonPath("$[1].eventName",is("eventName2")))
                .andExpect(jsonPath("$[2].eventName",is("eventName1")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_be_vote_successfully() throws Exception {
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
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
        userRepository.save(userPO);
        rsEventRepository.save(rsEventPO);
        Vote vote = Vote.builder().rsEventId(rsEventPO.getId()).voteNum(15).userId(userPO.getId()).time(now).build();
        String jsonString = objectMapper.writeValueAsString(vote);

        mockMvc.perform(patch("/rs/vote/{rsEventId}",vote.getRsEventId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid voteNumber")));
    }

    @Test
    public void should_buy_successfully() throws Exception{
        UserPO userPO2 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).voteNum(2).build();
        RsEventPO rsEventPO3 = RsEventPO.builder().eventName("eventName3").keyWord("keyWord3").userPO(userPO2).rank(1).amount(100).build();
        userRepository.save(userPO);
        userRepository.save(userPO2);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        rsEventRepository.save(rsEventPO3);

        final Trade trade = Trade.builder().amount(50).rank(2).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(trade);
        mockMvc.perform(post("/rs/buy/{id}",rsEventPO.getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(rsEventRepository.findByEventName("eventName1").get().getAmount(),50);
        assertEquals(rsEventRepository.findByEventName("eventName1").get().getRank(),2);
    }

    @Test
    public void should_replace_original_rank() throws Exception{
        UserPO userPO2 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).voteNum(2).build();
        RsEventPO rsEventPO3 = RsEventPO.builder().eventName("eventName3").keyWord("keyWord3").userPO(userPO2).rank(1).amount(100).build();
        userRepository.save(userPO);
        userRepository.save(userPO2);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        rsEventRepository.save(rsEventPO3);

        final Trade trade = Trade.builder().amount(150).rank(1).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(trade);
        mockMvc.perform(post("/rs/buy/{id}",rsEventPO.getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(rsEventRepository.findAll().size(),2);
        assertEquals(rsEventRepository.findByEventName("eventName1").get().getRank(), 1);
        assertEquals(rsEventRepository.findByEventName("eventName1").get().getAmount(), 150);
    }

    @Test
    public void should_buy_fail_for_invalid_amount() throws Exception{
        UserPO userPO2 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        RsEventPO rsEventPO2 = RsEventPO.builder().eventName("eventName2").keyWord("keyWord2").userPO(userPO).voteNum(2).build();
        RsEventPO rsEventPO3 = RsEventPO.builder().eventName("eventName3").keyWord("keyWord3").userPO(userPO2).rank(1).amount(100).build();
        userRepository.save(userPO);
        userRepository.save(userPO2);
        rsEventRepository.save(rsEventPO);
        rsEventRepository.save(rsEventPO2);
        rsEventRepository.save(rsEventPO3);

        final Trade trade = Trade.builder().amount(50).rank(1).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(trade);
        mockMvc.perform(post("/rs/buy/{id}",rsEventPO.getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid amount")));
    }
}