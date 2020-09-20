package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.UserNotValidException;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public int addUser(User user) {
        if (userRepository.existsByUserName(user.getName())) {
            throw new UserNotValidException("username has been used");
        }
        UserPO userPO = new UserPO();
        userPO.setUserName(user.getName());
        userPO.setGender(user.getGender());
        userPO.setAge(user.getAge());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNumber(user.getVoteNumber());
        userRepository.save(userPO);
        final int size = userRepository.findAll().size();
        return userRepository.findAll().get(size - 1).getId();
    }

    public void deleteUserById(int id){
        if (!userRepository.findById(id).isPresent()){
            throw new UserNotValidException("user id not valid");
        }
        userRepository.deleteById(id);
    }

    public UserPO getUserById(int id){
        Optional<UserPO> userPO = userRepository.findById(id);
        if (!userPO.isPresent()){
            throw new UserNotValidException("user id not valid");
        }
        return userPO.get();
    }

    public List<UserPO> getUserList(){
        return userRepository.findAll();
    }
}
