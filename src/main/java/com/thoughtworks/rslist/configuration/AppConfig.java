package com.thoughtworks.rslist.configuration;

import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRecordRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class AppConfig {
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    TradeRecordRepository tradeRecordRepository;
    @Bean
    @DependsOn(value = {
            "rsEventRepositoryBean",
            "userRepositoryBean",
            "voteRepositoryBean",
            "tradeRecordRepositoryBean"
    })
    public RsService rsService(){
        return new RsService(rsEventRepository,userRepository,voteRepository,tradeRecordRepository);
    }
    @Bean("rsEventRepositoryBean")
    public RsEventRepository rsEventRepository(){
        return rsEventRepository;
    }
    @Bean("userRepositoryBean")
    public UserRepository userRepository(){
        return userRepository;
    }
    @Bean("voteRepositoryBean")
    public VoteRepository voteRepository(){
        return voteRepository;
    }
    @Bean("tradeRecordRepositoryBean")
    public TradeRecordRepository tradeRecordRepository(){
        return tradeRecordRepository;
    }
}
