package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "rsEvent")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsEventPO {
    @Id
    @GeneratedValue
    private int id;
    private String eventName;
    private String keyWord;
    @ManyToOne
    private UserPO userPO;
}
