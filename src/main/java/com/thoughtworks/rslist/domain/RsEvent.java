package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
public class RsEvent {
    @NotBlank(groups = CreateAction.class)
    private String eventName;
    @NotBlank(groups = CreateAction.class)
    private String keyWord;

    @NotNull
    @Valid
    private int userId;

    public RsEvent(String eventName, String keyWord, int userId) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.userId = userId;
    }

    public RsEvent() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
