package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.ReturnRsEventData;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private List<RsEvent> rsList;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    UserPO userPO;
    RsEventPO rsEventPO;


    @BeforeEach
    public void beforeEach(){
        rsList = new ArrayList<>();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        userPO = UserPO.builder().userName("kong").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        rsEventPO = RsEventPO.builder().eventName("eventName1").keyWord("keyWord1").userPO(userPO).build();
    }

    @AfterEach
    public void afterEach(){
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

    @Ignore
    public void get_rsEvent_not_contain_user() throws Exception {
        rsList.add(new RsEvent("第一条事件", "无标签",1));
        rsList.add(new RsEvent("第二条事件", "无标签",1));
        rsList.add(new RsEvent("第三条事件", "无标签",1));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsList);

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0]",not(hasKey("user"))))
                .andExpect(jsonPath("$[1]",not(hasKey("user"))))
                .andExpect(jsonPath("$[2]",not(hasKey("user"))))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Ignore //这是检验user姓名的
    public void should_throw_method_argument_not_valid_exception() throws Exception{
        User user = new User("kongllllll", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }
}