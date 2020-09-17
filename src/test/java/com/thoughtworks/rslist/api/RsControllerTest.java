package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.RsEventList;
import com.thoughtworks.rslist.domain.User;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RsControllerTest {
    private List<RsEvent> rsList;

    @BeforeEach
    public void setUp(){
        rsList = new ArrayList<>();
        RsEventList.reSetRsEventList();
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    public void should_get_rs_event_list() throws Exception {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsList.add(new RsEvent("第一条事件", "无标签",user));
        rsList.add(new RsEvent("第二条事件", "无标签",user));
        rsList.add(new RsEvent("第三条事件", "无标签",user));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsList);

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_rs_event_between() throws Exception {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsList.add(new RsEvent("第一条事件", "无标签",user));
        rsList.add(new RsEvent("第二条事件", "无标签",user));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsList);

        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_rs_event() throws Exception{
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index","4"));

        mockMvc.perform(get("/rs/4"))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_modify_rs_event() throws Exception{
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(patch("/rs/1/").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName",is("猪肉涨价了")))
                .andExpect(jsonPath("$.keyWord",is("经济")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_rs_event() throws Exception{
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsList.add(new RsEvent("第二条事件", "无标签",user));
        rsList.add(new RsEvent("第三条事件", "无标签",user));
        ObjectMapper objectMapper = new ObjectMapper();
        String returnJsonString = objectMapper.writeValueAsString(rsList);

        mockMvc.perform(delete("/rs/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无标签")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(returnJsonString))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_user_when_user_not_in_userList() throws Exception{
        User user = new User("ling", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/4"))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(content().json(objectMapper.writeValueAsString(userList)))
                .andExpect(status().isOk());
    }

    @Test
    public void should_not_add_user_when_user_in_userList() throws Exception{
        User user = new User("ling", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/4"))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/5"))
                .andExpect(content().json(jsonString))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(userList)))
                .andExpect(status().isOk());
    }

    @Test
    public void eventName_should_not_be_null() throws Exception{
        User user = new User("ling", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent();
        rsEvent.setKeyWord("经济");
        rsEvent.setUser(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void keyWord_should_not_be_null() throws Exception{
        User user = new User("ling", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("经济");
        rsEvent.setUser(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void User_should_not_be_null() throws Exception{
        RsEvent rsEvent = new RsEvent();
        rsEvent.setEventName("经济大爆炸");
        rsEvent.setKeyWord("经济");
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Ignore
    public void get_rsEvent_not_contain_user() throws Exception {
        User user = new User("kong", "male", 22, "107978987@qq.com", "13576877788");
        rsList.add(new RsEvent("第一条事件", "无标签",user));
        rsList.add(new RsEvent("第二条事件", "无标签",user));
        rsList.add(new RsEvent("第三条事件", "无标签",user));
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

    @Test
    public void email_should_be_normalized() throws Exception {
        User user = new User("kong", "male", 20, "107978987", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String rsEventJsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(rsEventJsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_rsEvent_not_valid_exception() throws Exception{
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid index")));
    }

    @Test
    public void should_throw_method_argument_not_valid_exception() throws Exception{
        User user = new User("kongllllll", "male", 22, "107978987@qq.com", "13576877788");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济",user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    public void should_throw_invalid_request_param_exception() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=1&end=4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=2&end=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }
}