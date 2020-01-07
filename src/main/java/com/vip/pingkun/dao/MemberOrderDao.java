package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.MemberOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOrderDao extends JpaRepository<MemberOrder,Long> {

    MemberOrder findMemberOrderByOrderNumber(String orderNum);

}
