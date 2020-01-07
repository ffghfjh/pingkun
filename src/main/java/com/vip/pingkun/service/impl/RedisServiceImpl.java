package com.vip.pingkun.service.impl;

import com.vip.pingkun.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {
    Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public String getOpenId(String str) {
        return (str.split("%"))[0];
    }

    @Override
    public String getSessionKey(String str) {
        return (str.split("%"))[1];
    }

    @Override
    public String getUserId(String str) {
        return (str.split("%"))[2];
    }

}
