package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.SearchCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchCardDao extends JpaRepository<SearchCard,Long> {

    String serToday = "select * from search_card where TO_DAYS(create_time) = TO_DAYS(NOW()) and user_id = ?1";
    String serYear = "select * from search_card where YEAR(create_time) = YEAR(NOW()) and user_id = ?1";
    @Query(value = serToday,nativeQuery = true)
    List<SearchCard> getToDaySerch(Long userId);
    @Query(value = serYear,nativeQuery = true)
    List<SearchCard> getYearSerch(Long userId);


}
