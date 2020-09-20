package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime time;
    private int voteNum;
    @ManyToOne @JoinColumn(name = "user_id") private UserPO userPO;
    @ManyToOne @JoinColumn(name = "rs_event_id") private RsEventPO rsEventPO;
}
