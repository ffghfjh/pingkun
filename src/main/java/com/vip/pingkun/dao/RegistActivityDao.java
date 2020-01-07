package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.InfoMation;
import com.vip.pingkun.pojo.RegistActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface RegistActivityDao extends JpaRepository<RegistActivity,Long> {

    RegistActivity findRegistActivityByOrderNo(String orderNo);

    String queery = "select * from regist_activity limit ?1,?2";
    @Query(value = queery,nativeQuery = true)
    List<RegistActivity> findAllByPage(int str, int limit);

    String queery1 = "select * from regist_activity where activity_id = ?1 limit ?2,?3";
    @Query(value = queery1,nativeQuery = true)
    List<RegistActivity> findByActIdAndPage(Long id,int str,int limit);

    String queery2 = "select a.id,a.address,a.context,a.create_time,a.img,a.money,a.publish_man,a.time,a.title,a.update_time,a.state from info_mation a inner join regist_activity b where b.user_id = ?1 and b.state = 1 and a.id = b.activity_id order by create_time desc";

    @Query(value = queery2,nativeQuery = true)
    List<Map<String,Object>> getUserRegistActivity(Long uesrId);

    @Query(value = queery2,nativeQuery = true)
    List<RegistActivity> findRegistActivitiesByUserId(Long userId);

}
