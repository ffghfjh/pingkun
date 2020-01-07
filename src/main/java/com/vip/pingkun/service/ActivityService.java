package com.vip.pingkun.service;

import com.vip.pingkun.util.Result;

public interface ActivityService {

    Result registActivity(Long activityId, boolean needBill, String token);


    /**
     * 获取热门资讯
     * @return
     */
    Result getHotActivitys();



}
