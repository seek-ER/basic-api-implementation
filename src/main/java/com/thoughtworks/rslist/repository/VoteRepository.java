package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.po.VotePO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VoteRepository extends PagingAndSortingRepository<VotePO,Integer> {
    @Override
    List<VotePO> findAll();

    List<VotePO> findAllByUserPOIdAndRsEventPOId(int userId, int rsEventId, Pageable pageable);
}
