package com.vip.pingkun.service;

import java.util.Set;

/**
 * redis有关服务
 */
public interface RedisService {


     String getOpenId(String str);

     String getSessionKey(String str);

     String getUserId(String str);

}
