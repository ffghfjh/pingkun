package com.vip.pingkun.service.impl;

import com.vip.pingkun.constant.Constant;
import com.vip.pingkun.dao.ActivityDao;
import com.vip.pingkun.dao.MemberOrderDao;
import com.vip.pingkun.dao.RegistActivityDao;
import com.vip.pingkun.dao.UserDao;
import com.vip.pingkun.pojo.MemberOrder;
import com.vip.pingkun.pojo.RegistActivity;
import com.vip.pingkun.pojo.User;
import com.vip.pingkun.service.WxPayService;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.WxPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class WxPayServiceImpl implements WxPayService {

    Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Value("${wxchat.appid}")
    String appid;
    @Value("${wxchat.secret}")
    String secret;
    @Value("${wxchat.key}")
    String key;

    @Autowired
    RegistActivityDao registActivityDao;
    @Autowired
    ActivityDao activityDao;
    @Autowired
    MemberOrderDao memberOrderDao;
    @Autowired
    UserDao userDao;
    @Override
    public void vipPayCallBack(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);
            Map<String, String> map = WxPayUtil.xmlToMap(notityXml);
            String returnCode = map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                //验证签名是否正确
                //Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
                //String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = WxPayUtil.getSign(map, key);
                // 因为微信回调会有八次之多,所以当第一次回调成功了,那么我们就不再执行逻辑了

                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if (sign.equals(map.get("sign"))) {
                    /**此处添加自己的业务逻辑代码start**/
                    String orderNum = map.get("out_trade_no");
                    MemberOrder order = memberOrderDao.findMemberOrderByOrderNumber(orderNum);
                    if(order!=null){
                        if(order.getState()==0){
                            Long userId = order.getUserId(); //开通的用户ID
                            int year = order.getYear();//开通的年份
                            int grade = order.getGrade();//开通的会员类型
                            User user = userDao.findUserById(userId);
                            if(user!=null){
                                //检测用户当前是否是非会员
                                if(user.getGrade()== Constant.NON_MEMBER){
                                    user.setGrade(grade);
                                    Date data = new Date();
                                    Calendar rightNow = Calendar.getInstance();
                                    rightNow.setTime(data);
                                    rightNow.add(Calendar.YEAR,1);
                                    user.setBecomeGradeTime(data);
                                    user.setEndGradeTime(rightNow.getTime());
                                    userDao.save(user);
                                    order.setState(1);//设置订单为完成态
                                    memberOrderDao.save(order);
                                }
                            }else {
                                logger.info("开通会员支付回调,查询用户失败");
                            }
                        }
                    }else {
                        logger.info("支付回调查询订单失败");
                    }
                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                } else {
                    System.out.println("微信支付回调失败!签名不一致");
                }
                System.out.println(resXml);
                System.out.println("微信支付回调数据结束");

                BufferedOutputStream out = new BufferedOutputStream(
                        response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registPayCallBack(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            System.out.println("接收到的报文：" + notityXml);
            Map<String, String> map = WxPayUtil.xmlToMap(notityXml);
            String returnCode = map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                //验证签名是否正确
                //Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
                //String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = WxPayUtil.getSign(map, key);
                // 因为微信回调会有八次之多,所以当第一次回调成功了,那么我们就不再执行逻辑了

                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if (sign.equals(map.get("sign"))) {
                    /**此处添加自己的业务逻辑代码start**/
                    String orderNum = map.get("out_trade_no");
                    RegistActivity registActivity = registActivityDao.findRegistActivityByOrderNo(orderNum);
                    if(registActivity!=null){
                       if(registActivity.getState()==0){
                           registActivity.setState(1);
                           registActivityDao.save(registActivity);
                       }
                    }else {
                        logger.info("支付回调查询报名记录失败: registActivity is null");
                    }
                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                } else {
                    System.out.println("微信支付回调失败!签名不一致");
                }
                System.out.println(resXml);
                System.out.println("微信支付回调数据结束");

                BufferedOutputStream out = new BufferedOutputStream(
                        response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
