package com.vip.pingkun.service.impl;

import com.vip.pingkun.dao.*;
import com.vip.pingkun.pojo.*;
import com.vip.pingkun.service.AdminService;
import com.vip.pingkun.util.FileUploadUtil;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    AdminDao adminDao;
    @Autowired
    UserDao userDao;
    @Autowired
    ActivityDao activityDao;
    @Autowired
    RegistActivityDao registActivityDao;
    @Autowired
    CallingCardDao callingCardDao;

    Result result;

    @Override
    public Result adminLogin(String userName, String password, HttpServletRequest request) {
        Admin admin = adminDao.findAdminByUserNameAndPassWord(userName,password);
        if(admin!=null){
            request.getSession().setAttribute("adminU",admin.getUserName());
            return Result.success();
        }
        return Result.failure(ResultCode.USER_LOGIN_ERROR);
    }

    @Override
    public Object getUserListByPage(Long title,int page,int limit) {
        Map<String,Object> reMap = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        List<User> users;
        if(title==null){
            users = userDao.findUsersByPage((page-1)*limit,limit);
        }else {
            users = new ArrayList<>();
            User user = userDao.findUserById(title);
            if(user!=null){
                users.add(user);
            }
        }
        logger.info("获取用户信息："+users.size());
        if(users!=null&&users.size()>0){
            for(User user : users){
                Map<String,Object> map = new HashMap<>();
                map.put("id",user.getId());
                map.put("com_name",user.getCommpanyName());
                map.put("email",user.getEmail());
                map.put("header",user.getHeader());
                map.put("job_name",user.getJobName());
                map.put("main_project",user.getMainProject());
                map.put("nick_name",user.getNickName());
                map.put("notice_area",user.getNoticeArea());
                map.put("phone",user.getPhone());
                if(user.getSex()==0){
                    map.put("sex","男");
                }else {
                    map.put("sex","女");
                }
                map.put("create_time",user.getCreateTime());
                switch (user.getGrade()){
                    case 0:
                        map.put("grade","普通用户");
                        break;
                    case 1:
                        map.put("grade","Vip用户");
                        break;
                    case 2:
                        map.put("grade","白银用户");
                        break;
                    case 3:
                        map.put("grade","黄金用户");
                        break;
                    case 4:
                        map.put("grade","砖石用户");
                        break;
                }
                map.put("end_grade_time",user.getEndGradeTime());
                if(user.getState()==-1){
                    map.put("state","已封号");
                }else {
                    map.put("state","正常使用");
                }
                list.add(map);
            }
        }
        reMap.put("count",userDao.getCount());
        reMap.put("code",0);
        reMap.put("msg","成功");
        reMap.put("data",list);
        return reMap;
    }

    @Override
    public Result getFileUploadToken() {
        return Result.success(FileUploadUtil.getFileToken());
    }

    @Override
    public Result uploadFile(MultipartFile file) {
        String filePath = FileUploadUtil.uploadFile(file);
        if(filePath!=null){
            Map<String,Object> map = new HashMap<>();
            map.put("src",filePath);
            map.put("title",file.getOriginalFilename());
            result = new Result();
            result.setCode(0);
            result.setMsg("成功");
            result.setData(map);
            return result;
        }
        return Result.failure(ResultCode.FAILURE);
    }

    @Override
    public Result addNews(String title, String pubMan, Date time,String address, int money,String context) {
        InfoMation infoMation = new InfoMation();
        infoMation.setTitle(title);
        infoMation.setPublishMan(pubMan);
        infoMation.setTime(time);
        infoMation.setContext(context);
        infoMation.setAddress(address);
        infoMation.setState(1);//设置为开启状态
        Date date = new Date();
        infoMation.setCreateTime(date);
        infoMation.setUpdateTime(date);
        infoMation.setMoney(money);
        activityDao.save(infoMation);
        return Result.success();
    }

    @Override
    public Object getActivitys(int page,int limit,Long title) {
        List<InfoMation> infoMations;
        if(title==null){
            infoMations = activityDao.findInfosByPage((page-1)*limit,limit);
        }else {
            infoMations = new ArrayList<>();
            InfoMation infoMation = activityDao.findInfoMationById(title);
            if(infoMation!=null){
                infoMations.add(activityDao.findInfoMationById(title));
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("msg","成功");

        int count = activityDao.getCount();


        map.put("count",count);

        map.put("data",infoMations);

        return map;
    }

    @Override
    public Result getActivityById(Long id) {
        InfoMation infoMation = activityDao.findInfoMationById(id);
        if(infoMation==null){
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        return Result.success(infoMation);
    }

    @Override
    public Result editNews(String title, String pubMan, Date time, String address, int money, String context, Long id) {
        InfoMation infoMation = activityDao.findInfoMationById(id);
        if(infoMation==null){
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        infoMation.setTitle(title);
        infoMation.setPublishMan(pubMan);
        infoMation.setTime(time);
        infoMation.setAddress(address);
        infoMation.setMoney(money);
        infoMation.setContext(context);
        activityDao.save(infoMation);
        return Result.success();
    }

    @Override
    public Result uptActivityState(Integer state, Long id) {
        InfoMation infoMation = activityDao.findInfoMationById(id);
        if(infoMation==null){
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        infoMation.setState(state);
        activityDao.save(infoMation);
        return Result.success();
    }

    @Override
    public Object getActivityRegists(int page, int limit,Long id) {
        Map<String,Object> map = new HashMap<>();
        List<RegistActivity> registActivities;
        if(id==null){
            registActivities = registActivityDao.findAllByPage((page-1)*limit,limit) ;
        }else {
            registActivities = registActivityDao.findByActIdAndPage(id,(page-1)*limit,limit);
        }


        if(registActivities==null){
            map.put("count",0);
        }else {
            map.put("count",registActivities.size());
        }
        map.put("code",0);
        map.put("msg","成功");
        map.put("data",registActivities);
        return map;
    }

    @Override
    public Result addCallingCard(String name, String comName, String jobName, String phone, String imgUrl) {
        CallingCard callingCard = new CallingCard();
        callingCard.setName(name);
        callingCard.setCommpanyName(comName);
        callingCard.setJobName(jobName);
        callingCard.setPhone(phone);
        callingCard.setFilePath(imgUrl);
        Date date = new Date();
        callingCard.setCreateTime(date);
        callingCard.setUpdateTime(date);
        callingCardDao.save(callingCard);
        return Result.success();
    }

    @Override
    public Object getCallingCards(int page, int limit, String title) {
        Map<String,Object> map = new HashMap<>();
        List<CallingCard> callingCards;
        if(title==null||title.equals("")){
           callingCards = callingCardDao.getCardsByPage((page-1)*limit,limit);
        }else {
            callingCards = callingCardDao.getCardByPageAndName((page-1)*limit,limit,"%"+title+"%");
        }
        int count = callingCardDao.getCount();

        map.put("count",count);

        map.put("msg","成功");
        map.put("code",0);
        map.put("data",callingCards);
        return map;
    }

    @Override
    public Result editCard(Long id, String name, String comName, String jobName, String phone, String imgUrl) {
        CallingCard card = callingCardDao.findCallingCardById(id);
        if(card==null){
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        card.setName(name);
        card.setCommpanyName(comName);
        card.setJobName(jobName);
        card.setPhone(phone);
        card.setFilePath(imgUrl);
        callingCardDao.save(card);
        return Result.success();
    }

    @Override
    public Result editCardImg(Long id, String imgUrl) {
        CallingCard card = callingCardDao.findCallingCardById(id);
        if(card==null){
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        card.setFilePath(imgUrl);
        callingCardDao.save(card);
        return Result.success();
    }

    @Override
    public Result removeCard(Long id) {
        callingCardDao.deleteById(id);
        return Result.success();
    }



    public Result editPassword(String oldPwd,String newPwd,String userName){
       Admin admin = adminDao.findAdminByUserName(userName);
       if(admin!=null){
           if(!admin.getPassWord().equals(oldPwd)){
               result = new Result();
               result.setCode(0);
               result.setMsg("原密码错误");
               return result;
           }
           admin.setPassWord(newPwd);
           adminDao.save(admin);
           return Result.success();
       }
       return Result.failure(ResultCode.RESULE_DATA_NONE);
    }
}
