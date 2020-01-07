package com.vip.pingkun.controller;

import com.vip.pingkun.util.PassToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/wxPay/")
public class PayController {
    Logger logger = LoggerFactory.getLogger(PayController.class);
    @PostMapping("/vipPayCallback")
    @PassToken
    public void vipPayCallback(HttpServletRequest request, HttpServletResponse response){
        logger.info("微信支付回调");
    }

}
