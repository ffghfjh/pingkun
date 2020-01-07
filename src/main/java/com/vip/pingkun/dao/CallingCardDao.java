package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.CallingCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CallingCardDao extends JpaRepository<CallingCard,Long> {
    List<CallingCard> findCallingCardsByNameLikeOrCommpanyNameLikeOrPhoneLike(String name,String comName,String phone);

    String queery = "select * from calling_card limit ?1,?2";
    @Query(value = queery,nativeQuery = true)
    List<CallingCard> getCardsByPage(int str,int limit);

    String queery1 = "select * from calling_card where name like ?3 limit ?1,?2";
    @Query(value = queery1,nativeQuery = true)
    List<CallingCard> getCardByPageAndName(int str,int limit,String title);

    CallingCard findCallingCardById(Long id);
    String querryCount = "select count(*) from calling_card";

    @Query(value = querryCount,nativeQuery = true)
    int getCount();

    void deleteById(Long id);

}
