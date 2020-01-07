package com.vip.pingkun.controller;

import com.vip.pingkun.pojo.RegistActivity;
import com.vip.pingkun.service.ActivityService;
import com.vip.pingkun.util.PassToken;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.UserLoginToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "活动控制器",tags = {"有关资讯接口"})
public class ActivityController {
    @Autowired
    ActivityService activityService;
    @PostMapping("registActivity")
    @UserLoginToken
    @ApiOperation(value = "活动报名",tags = "",notes = "needBill 是否需要发票 boolean类型")
    public Result registActivity(Long activityId,boolean needBill,HttpServletRequest request){
        return activityService.registActivity(activityId,needBill,request.getHeader("token"));
    }

    @GetMapping("getHotActivity")
    @PassToken
    @ApiOperation(value="获取热门资讯")
    public Result getHotActivity(){
        return activityService.getHotActivitys();
    }


}
