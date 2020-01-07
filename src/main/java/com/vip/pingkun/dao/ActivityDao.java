package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.InfoMation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.sound.sampled.Line;
import java.util.List;

/**
 * 活动dao层
 */
public interface ActivityDao extends JpaRepository<InfoMation,Long> {

    InfoMation findInfoMationById(Long id);

    String queery = "select * from info_mation limit ?1,?2";
    @Query(value = queery,nativeQuery = true)
    List<InfoMation> findInfosByPage(int str, int limit);

    String queery1 = "select a.id,a.address,a.context,a.create_time,a.img,a.money,a.img,a.money,a.publish_man,a.time,a.title,a.update_time,a.state from info_mation a,regist_activity b where a.state = 1 group by a.id order by (select count(*) from regist_activity  where activity_id = a.id and state = 1) desc";
    @Query(value = queery1,nativeQuery = true)
    List<InfoMation> getHotInfoMation();

    String queeryCount = "select count(*) from info_mation";
    @Query(value = queeryCount,nativeQuery = true)
    int getCount();

}
