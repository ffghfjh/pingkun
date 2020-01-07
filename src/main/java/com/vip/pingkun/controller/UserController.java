package com.vip.pingkun.controller;

import com.vip.pingkun.service.UserService;
import com.vip.pingkun.util.PassToken;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.UserLoginToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "用户接口")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @GetMapping("/login")
    @PassToken
    @ApiOperation("用户登录")
    public Result login(String code, String signature, String encryptedData, String iv){
        logger.info("code:"+code+",sihnature:"+signature+",encryptedData:"+encryptedData+",iv:"+iv);
        return userService.login(code,signature,encryptedData,iv);
    }


    @GetMapping("getMyInfo")
    @UserLoginToken
    @ApiOperation("获取用户信息")
    public Result getMyInfo(HttpServletRequest request){
      return userService.getMyInfo(request.getHeader("token"));
    }

    @PostMapping("setOherInfo")
    @UserLoginToken
    @ApiOperation("完善用户资料")
    public Result setOtherInfo(String commpanyName,String jobName,String mainProduct,
                               String email,String phone,String noticeArea,HttpServletRequest request){
       return userService.updMyOtherInfo(commpanyName,jobName,mainProduct,email,phone,noticeArea,request.getHeader("token"));
    }

    @PostMapping("becomeMember")
    @UserLoginToken
    @ApiOperation("开通会员")
    public Result becomeMember(HttpServletRequest request,int grade,int year){
       return userService.becomeMember(request.getHeader("token"),grade,year);
    }

    @GetMapping("/getMyRegsit")
    @UserLoginToken
    @ApiOperation("获取我的报名")
    public Result getMyRegsit(HttpServletRequest request){
        return userService.getMyRegistActivity(request.getHeader("token"));
    }

}
