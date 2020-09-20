package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "trade_record")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecordPO {
    private int id;
    private int amount;
    private int rank;
    private int rsEventPOId;

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
}
