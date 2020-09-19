package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private int userId;
    private int rsEventId;
    private String time;
    private int voteNum;
    @JsonIgnore
    public int getRsEventId() {
        return rsEventId;
    }

    @JsonIgnore
    public void setRsEventId(int rsEventId) {
        this.rsEventId = rsEventId;
    }
}
