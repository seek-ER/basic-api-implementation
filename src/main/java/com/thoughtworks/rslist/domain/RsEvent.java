package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RsEvent {
    @NotBlank
    private String eventName;
    @NotBlank
    private String keyWord;
    @NotNull
    @Valid
    private User user;

    public RsEvent(String eventName, String keyWord, User user) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.user = user;
    }

    public RsEvent() {
    }

//    @JsonIgnore
    public User getUser() {
        return user;
    }

//    @JsonProperty
    public void setUser(User user) {
        this.user = user;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
