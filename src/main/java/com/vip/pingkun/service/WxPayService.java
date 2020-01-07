package com.vip.pingkun.service;

import com.vip.pingkun.util.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WxPayService {
    @Transactional
    void vipPayCallBack(HttpServletRequest request, HttpServletResponse response);
    @Transactional
    void registPayCallBack(HttpServletRequest request, HttpServletResponse response);
}
