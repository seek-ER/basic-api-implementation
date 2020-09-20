package com.thoughtworks.rslist.repository;


import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RsEventRepository extends PagingAndSortingRepository<RsEventPO,Integer> {
    @Override
    List<RsEventPO> findAll();

    Optional<RsEventPO> findByRank(int rank);

    void deleteByRank(int rank);

    @Query("select e from RsEventPO e where e.rank=0 ORDER BY e.voteNum desc")
    List<RsEventPO> sortNotBuyRsEvent();

    @Query("select e from RsEventPO e where e.rank>0")
    List<RsEventPO> findBuyRsEvent();

    @Query("select e from RsEventPO e ORDER BY e.rank asc")
    List<RsEventPO> findAllSortedByRank();

    Optional<RsEventPO> findByEventName(String eventName);
}
