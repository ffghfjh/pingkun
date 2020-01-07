package com.vip.pingkun.dao;

import com.vip.pingkun.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDao extends JpaRepository<Admin,Long> {
    Admin findAdminByUserNameAndPassWord(String name,String pwd);

    Admin findAdminByUserName(String userName);
}
