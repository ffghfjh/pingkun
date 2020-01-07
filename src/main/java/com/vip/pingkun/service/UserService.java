package com.vip.pingkun.service;

import com.vip.pingkun.util.Result;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    /**
     * 微信登录
     * @param code
     * @return
     */
    @Transactional
    Result login(String code, String signature, String encryptedData, String iv);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    Result getMyInfo(String token);

    /**
     * 更新用户资料
     * @param commpanyName  公司名称
     * @param jobName  职务
     * @param mainProduct  主营产品
     * @param email  邮箱
     * @param phone  电话
     * @param noticeArea  关注领域
     * @param token
     * @return
     */
    Result updMyOtherInfo(String commpanyName,String jobName,String mainProduct,String email,String phone,String noticeArea,String token);


    /**
     * 成为会员
     * @param token
     * @param grade 会员等级
     * @return
     */
    Result becomeMember(String token,int grade,int year);


    /**
     * 获取报名记录
     * @param token
     * @return
     */
    Result getMyRegistActivity(String token);

}
