package com.vip.pingkun.controller;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.vip.pingkun.service.AdminService;
import com.vip.pingkun.util.*;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/admin")
@Api(tags = "后台接口")
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService  adminService;
    @PostMapping("/login")
    public Result login(String userName, String password, HttpServletRequest request){
        return adminService.adminLogin(userName,password,request);
    }

    @GetMapping("/getUserList")
    public Object getUserList(int page,int limit,Long title){
       logger.info("管理员获取用户列表...");
       return adminService.getUserListByPage(title,page,limit);
    }

    @PostMapping("/uploadImg")
    public Result uploadImg(@RequestParam("file") MultipartFile file){
       return adminService.uploadFile(file);
    }

    @GetMapping("/getFileToken")
    public Result getFileToken(){
      return adminService.getFileUploadToken();
    }

    @PostMapping("/addNews")
    public Result addNews(String title, String pubMan, String time,String address,Integer money,String context){
        logger.info("发布资讯:"+title+"/"+pubMan+"/"+time+"/"+address+"/"+money+"/"+context);
        try {
            return adminService.addNews(title,pubMan,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time),address,money,context);
        } catch (ParseException e) {
            return Result.failure(ResultCode.DATA_IS_WRONG);
        }
    }

    @PostMapping("/editNews")
    public Result editNews(String title, String pubMan, String time,String address,Integer money,String context,Long id){
        logger.info("修改资讯:"+title+"/"+pubMan+"/"+time+"/"+address+"/"+money+"/"+context);
        if(id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        try {
            return adminService.editNews(title,pubMan,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time),address,money,context,id);
        } catch (ParseException e) {
            return Result.failure(ResultCode.DATA_IS_WRONG);
        }
    }
    /**
     * 获取资讯
     */
    @GetMapping("/getActivitys")
    public Object getActivitys(int page,int limit,Long title){
        return adminService.getActivitys(page,limit,title);
    }

    @GetMapping("/getActivityById")
    public Result getActivityById(Long id){
        if(id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        return adminService.getActivityById(id);
    }

    @PostMapping("/uptActivityState")
    public Result uptActivityState(Integer state,Long id){
        if(state==null||id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        //1开启状态
        if(state==1||state==0){
            return adminService.uptActivityState(state,id);
        }
        return Result.failure(ResultCode.PARAM_IS_INVALID);
    }

    @GetMapping("/getRegistActivitys")
    public Object getRegistActivitys(int page,int limit,Long title){
        return adminService.getActivityRegists(page,limit,title);
    }

    @PostMapping("/addCallingCard")
    public Result addCallingCard(String name,String comName,String jobName,String phone,String imgUrl){
       return adminService.addCallingCard(name,comName,jobName,phone,imgUrl);
    }

    @GetMapping("/getCards")
    public Object getCards(int page,int limit,String title){
        return adminService.getCallingCards(page,limit,title);
    }

    @PostMapping("/editCard")
    public Result editCard(Long id,String name,String comName,String jobName,String phone,String imgUrl ){

        if(id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        return adminService.editCard(id,name,comName,jobName,phone,imgUrl);
    }
    @PostMapping("/editCardImg")
    public Result editCardImg(Long id,String imgUrl ){

        if(id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        return adminService.editCardImg(id,imgUrl);
    }
    @PostMapping("/removeCard")
    public Result removeCard(Long id){
        if(id==null){
            return Result.failure(ResultCode.PARAM_IS_BLANK);
        }
        return adminService.removeCard(id);
    }

    @PostMapping("/editPassword")
    public Result editPassword(String oldPassWord,String newPwd,HttpServletRequest request){
        String userName = request.getSession().getAttribute("adminU").toString();
        return adminService.editPassword(oldPassWord,newPwd,userName);
    }

    @GetMapping("/authLogin")
    public Result authLogin(){
        return Result.success();
    }

}
