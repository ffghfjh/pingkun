package com.vip.pingkun.service;

import com.vip.pingkun.util.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface AdminService  {

    Result adminLogin(String userName, String password, HttpServletRequest request);


    /**
     * 分页查询用户列表
     * @return
     */
    Object getUserListByPage(Long  title,int page,int limit);

    /**
     * 获取文件上传token
     * @return
     */
    Result getFileUploadToken();

    /**
     *
     * @param file
     * @return
     */
    Result uploadFile(MultipartFile file);

    /**
     * 发布资讯
     * @param title
     * @param pubMan
     * @param time
     * @param context
     * @return
     */
    Result addNews(String title, String pubMan, Date time,String address,int money, String context);

    /**
     * 获取资讯
     * @return
     */
    Object getActivitys(int page,int limit,Long title);

    /**
     * 根据ID查询资讯
     * @param id
     * @return
     */
    Result getActivityById(Long id);


    /**
     * 更新活动信息
     * @param title
     * @param pubMan
     * @param time
     * @param address
     * @param money
     * @param context
     * @param id
     * @return
     */
    Result editNews(String title, String pubMan, Date time,String address,int money, String context,Long id);

    /**
     * 更新活动状态
     * @param state
     * @param id
     * @return
     */
    Result uptActivityState(Integer state,Long id);

    /**
     * 分页获取活动报名信息
     * @param page
     * @param limit
     * @return
     */
    Object getActivityRegists(int page,int limit,Long id);

    /**
     * 添加名片
     * @param name
     * @param comName
     * @param jobName
     * @param phone
     * @param imgUrl
     * @return
     */
    Result addCallingCard(String name,String comName,String jobName,String phone,String imgUrl);


    /**
     * 获取名片信息
     * @param page
     * @param limit
     * @param title
     * @return
     */
    Object getCallingCards(int page,int limit,String title);

    /**
     * 修改名片信息
     * @param id
     * @param name
     * @param comName
     * @param jobName
     * @param phone
     * @param imgUrl
     * @return
     */
    Result editCard(Long id,String name,String comName,String jobName,String phone,String imgUrl);

    /**
     * 修改名片图片
     * @param id
     * @param imgUrl
     * @return
     */
    Result editCardImg(Long id,String imgUrl);

    /**
     * 修改管理员密码
     * @param newPwd
     * @return
     */
    Result editPassword(String oldPwd,String newPwd,String userName);


    /**
     * 删除名片
     * @param id
     * @return
     */
    Result removeCard(Long id);





}
