package com.vip.pingkun.controller;

import com.vip.pingkun.service.CallingCarService;
import com.vip.pingkun.util.PassToken;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.UserLoginToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/card")
@Api(tags = "名片接口")
public class CallingCardController {
    Logger logger = LoggerFactory.getLogger(CallingCardController.class);

    @Autowired
    CallingCarService callingCarService;
    @GetMapping("/searchCard")
    @UserLoginToken
    @ApiOperation(value = "名片搜索")
    public Result searchCard(String title, HttpServletRequest request){
        logger.info("搜索数据："+title);
        return callingCarService.searchCar(title,request.getHeader("token"));
    }

}
