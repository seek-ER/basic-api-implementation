package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "rsEvent")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsEventPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String eventName;
    private String keyWord;
    @ManyToOne
    private UserPO userPO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsEventPO rsEventPO = (RsEventPO) o;
        return id == rsEventPO.id &&
                Objects.equals(eventName, rsEventPO.eventName) &&
                Objects.equals(keyWord, rsEventPO.keyWord) &&
                Objects.equals(userPO, rsEventPO.userPO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventName, keyWord, userPO);
    }

    @Override
    public String toString() {
        return "RsEventPO{" +
                "id=" + id +
                ", eventName='" + eventName + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", userPO_Id=" + userPO.getId() +
                '}';
    }
}
