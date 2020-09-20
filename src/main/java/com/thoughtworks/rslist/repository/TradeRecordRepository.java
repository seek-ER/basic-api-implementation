package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.po.TradeRecordPO;
import com.thoughtworks.rslist.po.VotePO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface TradeRecordRepository extends PagingAndSortingRepository<TradeRecordPO,Integer> {
    @Override
    Iterable<TradeRecordPO> findAll();
}
