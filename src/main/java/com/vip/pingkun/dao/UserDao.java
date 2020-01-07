package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface UserDao extends JpaRepository<User,Long> {


    User findUserById(Long id);



    User findUserByOpenId(String openId);


    String queery = "select * from user where nick_name like ?1 limit ?2,?3";
    String queery2 = "select * from user where id = ?1 or nick_name like ?2 limit ?3,?4";
    @Query(value = queery,nativeQuery = true)
    List<User> findUsersByNickNameLikePage(String title,int str,int limit);
    @Query(value = queery2,nativeQuery = true)
    List<User> findUsersByNickNameAndIdPage(String title,int str,int limit);
    String queery1 = "select * from user limit ?1,?2";

    @Query(value = queery1,nativeQuery = true)
    List<User> findUsersByPage(int str,int limit);

    String queeryCount = "select count(*) from user";
    @Query(value = queeryCount,nativeQuery = true)
    int getCount();




}
