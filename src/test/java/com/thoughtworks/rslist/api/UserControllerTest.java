package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RsEventRepository rsEventRepository;

    User user;
    UserPO userPO;

    @BeforeEach
    public void beforeEach(){
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        user = User.builder().name("kong").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        userPO = UserPO.builder().userName("kong").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
    }

    @AfterEach
    public void afterEach(){
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
    }

    @Test
    public void should_register_user() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        final boolean b = userRepository.existsByUserName(user.getName());
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserPO> all = userRepository.findAll();
        assertEquals(1,all.size());
        assertEquals("kong",all.get(0).getUserName());
        assertEquals("a@qq.com",all.get(0).getEmail());
    }

    @Test
    public void age_should_between_18_and_100() throws Exception {
        User user = new User("kong", "male", 15, "107978987@qq.com", "13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void name_should_not_more_than_8_character() throws Exception {
        User user = new User("konglllll", "male", 20, "107978987@qq.com", "13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void email_should_be_normalized() throws Exception {
        User user = new User("kong", "male", 20, "107978987", "13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void phone_should_be_normalized() throws Exception {
        User user = new User("kong", "male", 20, "107978987@qq.com", "3576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void name_should_not_be_null() throws Exception {
        User user = new User();
        user.setAge(20);
        user.setEmail("107978987@qq.com");
        user.setGender("male");
        user.setPhone("13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void gender_should_not_be_null() throws Exception {
        User user = new User();
        user.setAge(20);
        user.setEmail("107978987@qq.com");
        user.setName("male");
        user.setPhone("13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void should_throw_invalid_user() throws Exception {
        User user = new User("kongsgsgfdfg", "male", 22, "107978987@qq.com", "13576877788");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }

    @Test
    public void should_delete_user_by_id() throws Exception{
        UserPO userPO = UserPO.builder().userName("kong").age(20).phone("12698909973")
                .email("a@qq.com").gender("female").build();
        userRepository.save(userPO);
        RsEventPO rsEventPO = RsEventPO.builder().eventName("猪肉涨价了").keyWord("经济").userPO(userPO).build();
        rsEventRepository.save(rsEventPO);

        int usersSize = userRepository.findAll().size();
        int rsEventsSize = rsEventRepository.findAll().size();

        mockMvc.perform(delete("/user/{id}",usersSize)).andExpect(status().isOk());
        assertEquals(userRepository.findAll().size(),usersSize-1);
        assertEquals(rsEventRepository.findAll().size(),rsEventsSize-1);
    }

    @Test
    public void should_get_user_by_id() throws Exception{
        userRepository.save(userPO);
        mockMvc.perform(get("/user/{id}",userRepository.findAll().get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("kong")))
                .andExpect(jsonPath("$.age",is(22)))
                .andExpect(jsonPath("$.gender",is("male")));
    }

    @Test
    public void get_all_the_user() throws Exception{
        UserPO userPO1 = UserPO.builder().userName("kong2").age(22).phone("13576877788").email("a@qq.com").gender("male").build();
        userRepository.save(userPO);
        userRepository.save(userPO1);
        final List<UserPO> allUsers = userRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(allUsers);

        mockMvc.perform(get("/user")).andExpect(content().json(jsonString));
    }
}