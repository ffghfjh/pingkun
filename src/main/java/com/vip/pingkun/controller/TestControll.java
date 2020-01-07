package com.vip.pingkun.controller;

import com.vip.pingkun.dao.UserDao;
import com.vip.pingkun.pojo.User;
import com.vip.pingkun.util.PassToken;
import com.vip.pingkun.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControll {

    @Autowired
    UserDao userDao;

    @PostMapping("/addUser")
    @PassToken
    public Result addUser(String name,int sex,String header,String jobName,String mainProject,String phone){
        User user = new User();
        user.setJobName(jobName);
        user.setNickName(name);
        user.setPhone(phone);
        user.setSex(sex);
        user.setState(1);
        user.setHeader(header);
        user.setMainProject(mainProject);
        userDao.save(user);
        return Result.success();
    }
}
