package com.vip.pingkun.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.vip.pingkun.bean.OrderInfo;
import com.vip.pingkun.constant.Constant;
import com.vip.pingkun.dao.MemberOrderDao;
import com.vip.pingkun.dao.RegistActivityDao;
import com.vip.pingkun.dao.UserDao;
import com.vip.pingkun.pojo.InfoMation;
import com.vip.pingkun.pojo.MemberOrder;
import com.vip.pingkun.pojo.RegistActivity;
import com.vip.pingkun.pojo.User;
import com.vip.pingkun.service.RedisService;
import com.vip.pingkun.service.UserService;
import com.vip.pingkun.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${wxchat.appid}")
    String appId;
    @Value("${wxchat.secret}")
    String secret;
    @Value("${wxchat.mch_id}")
    String mchId;
    @Value("${ip}")
    String ip;
    @Value("${vipNoticeUrl}")
    String noticeUrl;
    @Value("${wxchat.key}")
    String key;
    @Value("${vip1}")
    String vip1;
    @Value("${vip2}")
    String vip2;
    @Value("${vip3}")
    String vip3;
    @Value("${vip4}")
    String vip4;
    String payUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    @Autowired
    UserDao userDao;
    @Autowired
    RedisService redisService;
    @Autowired
    MemberOrderDao memberOrderDao;
    @Autowired
    RegistActivityDao registActivityDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    Result result;

    @Override
    public Result login(String code, String signature, String encryptedData, String iv) {
        result = new Result();
        logger.info("code:" + code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        JSONObject jsonObject = HttpUtil.getToJSONObject(url);
        logger.info(jsonObject.toString());
        try {
            String openId = jsonObject.getString("openid");
            String sessionKey = jsonObject.getString("session_key");
            result.setCode(1);
            result.setMsg("成功");
            String token = JwtTokenUtil.createJWT(604800000);

            Map<String, Object> map = new HashMap<>();
            map.put("token", token);

            User user = userDao.findUserByOpenId(openId);
            /**
             * 判断是否存在该用户
             */
            if (user != null) {
                map.put("userId", user.getId());
                map.put("header", user.getHeader());
                map.put("grade", user.getGrade());
                //关联token和微信openid和session_key
                stringRedisTemplate.opsForValue().set(token, openId + "%" + sessionKey + "%" + user.getId());
            }
            //新用户
            else {
                logger.info(appId + "," + encryptedData + "," + sessionKey + "," + iv);
                String userInfo = WxCore.decrypt(appId, encryptedData, sessionKey, iv);
                logger.info(userInfo);
                try {
                    JSONObject info = JSON.parseObject(userInfo);
                    String nickName = info.getString("nickName");
                    int gender = info.getInteger("gender");
                    String city = info.getString("city");
                    String province = info.getString("province");
                    String header = info.getString("avatarUrl");

                    User newUser = new User();
                    Date date = new Date();
                    //男
                    if (gender == 0 || gender == 1) {
                        newUser.setSex(0);
                    }
                    //女
                    else {
                        newUser.setSex(1);
                    }
                    newUser.setHeader(header);
                    newUser.setNickName(nickName);
                    newUser.setCreateTime(date);
                    newUser.setUpdateTime(date);
                    newUser.setGrade(Constant.NON_MEMBER);
                    newUser.setBecomeGradeTime(date);
                    newUser.setEndGradeTime(date);
                    newUser.setOpenId(openId);
                    newUser.setState(1);
                    userDao.save(newUser);
                    User user1 = userDao.save(newUser);
                    map.put("userId", user1.getId());
                    map.put("header", user1.getHeader());
                    map.put("grade", user1.getGrade());
                    stringRedisTemplate.opsForValue().set(token, openId + "%" + sessionKey + "%" + user1.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    int errcode = jsonObject.getInteger("errcode");
                    String context = null;
                    switch (errcode) {
                        case -1:
                            context = "微信系统繁忙";
                            result.setCode(0);
                            result.setMsg(context);
                            break;
                        case 40029:
                            context = "code无效";
                            result.setCode(0);
                            result.setMsg(context);
                            break;
                        case 45011:
                            context = "登录频率限制";
                            result.setCode(0);
                            result.setMsg(context);
                            break;
                        case 40013:
                            context = "appid错误";
                            result.setCode(0);
                            result.setMsg(context);
                            logger.error(context);
                            break;
                    }
                }
            }
            result.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            int errcode = jsonObject.getInteger("errcode");
            String context = null;
            switch (errcode) {
                case -1:
                    context = "微信系统繁忙";
                    result.setCode(0);
                    result.setMsg(context);
                    break;
                case 40029:
                    context = "code无效";
                    result.setCode(0);
                    result.setMsg(context);
                    break;
                case 45011:
                    context = "登录频率限制";
                    result.setCode(0);
                    result.setMsg(context);
                    break;
                case 40013:
                    context = "appid错误";
                    result.setCode(0);
                    result.setMsg(context);
                    logger.error(context);
                    break;
            }
        }
        return result;
    }

    @Override
    public Result getMyInfo(String token) {
        Long userId = getUserId(token);
        if (userId != null) {
            User user = userDao.findUserById(userId);
            if (user != null) {
                if (user.getState() == -1) {
                    return Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN);
                }
                Map<String, Object> map = new HashMap<>();
                result = Result.success();
                map.put("header", user.getHeader());
                map.put("nickName", user.getNickName());
                map.put("sex", user.getSex());
                map.put("phone", user.getPhone());
                map.put("comName",user.getCommpanyName());
                map.put("jobName",user.getJobName());
                map.put("mainProject",user.getMainProject());
                map.put("email",user.getEmail());
                map.put("phone",user.getPhone());
                map.put("noticeArea",user.getNoticeArea());
                result.setData(map);
                return result;
            }
        }

        result = Result.failure(ResultCode.FAILURE);
        return result;
    }

    @Override
    public Result updMyOtherInfo(String commpanyName, String jobName, String mainProduct,
                                 String email, String phone, String noticeArea, String token) {
        Long userId = getUserId(token);
        if (userId != null) {
            User user = userDao.findUserById(userId);
            if (user != null) {
                if (user.getState() == -1) {
                    return Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN);
                }
                user.setCommpanyName(commpanyName);
                user.setJobName(jobName);
                user.setMainProject(mainProduct);
                user.setEmail(email);
                user.setPhone(phone);
                user.setNoticeArea(noticeArea);
                userDao.save(user);
                return Result.success();
            } else {
                result = Result.failure(ResultCode.FAILURE);
                result.setMsg("无此用户");
                return result;
            }
        }
        return Result.failure(ResultCode.FAILURE);
    }

    @Override
    public Result becomeMember(String token, int grade, int year) {
        Long userId = getUserId(token);
        if (userId != null) {
            User user = userDao.findUserById(userId);
            if (user != null) {
                if (user.getState() == -1) {
                    return Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN);
                }
                //商户订单号
                String outTradeNo = getOrderNo();
                MemberOrder order = new MemberOrder();
                Date date = new Date();
                Map orderInfo;
                Date nowDate = new Date();
                Date endTime = user.getEndGradeTime();

                if (user.getGrade() == Constant.NON_MEMBER||endTime.before(nowDate)) {
                    switch (grade) {
                        case Constant.VIP_MEMBER:
                            orderInfo = getOrder(Integer.parseInt(vip1) * year, user, outTradeNo);
                            if (orderInfo != null) {
                                order.setCreateTime(date);
                                order.setUpdateTime(date);
                                order.setGrade(Constant.VIP_MEMBER);
                                order.setOrderNumber(outTradeNo);
                                order.setState(0);
                                order.setMoney(Integer.parseInt(vip1) * year);
                                order.setYear(year);
                                memberOrderDao.save(order);
                                return Result.success(orderInfo);
                            } else {
                                result = new Result();
                                result.setMsg("下单失败");
                                result.setCode(0);
                                return result;
                            }
                        case Constant.SILVER_MEMBER:
                            orderInfo = getOrder(Integer.parseInt(vip2) * year, user, outTradeNo);
                            if (orderInfo != null) {
                                order.setCreateTime(date);
                                order.setUpdateTime(date);
                                order.setGrade(Constant.VIP_MEMBER);
                                order.setOrderNumber(outTradeNo);
                                order.setState(0);
                                order.setMoney(Integer.parseInt(vip2) * year);
                                order.setYear(year);
                                memberOrderDao.save(order);
                                return Result.success(orderInfo);
                            } else {
                                result = new Result();
                                result.setMsg("下单失败");
                                result.setCode(0);
                                return result;
                            }
                        case Constant.GOLD_MEMBER:
                            orderInfo = getOrder(Integer.parseInt(vip3) * year, user, outTradeNo);
                            if (orderInfo != null) {
                                order.setCreateTime(date);
                                order.setUpdateTime(date);
                                order.setGrade(Constant.VIP_MEMBER);
                                order.setOrderNumber(outTradeNo);
                                order.setState(0);
                                order.setMoney(Integer.parseInt(vip3) * year);
                                order.setYear(year);
                                memberOrderDao.save(order);
                                return Result.success(orderInfo);
                            } else {
                                result = new Result();
                                result.setMsg("下单失败");
                                result.setCode(0);
                                return result;
                            }
                        case Constant.DLIAMOND_MEMBER:
                            orderInfo = getOrder(Integer.parseInt(vip4) * year, user, outTradeNo);
                            if (orderInfo != null) {
                                order.setCreateTime(date);
                                order.setUpdateTime(date);
                                order.setGrade(Constant.VIP_MEMBER);
                                order.setOrderNumber(outTradeNo);
                                order.setState(0);
                                order.setMoney(Integer.parseInt(vip4) * year);
                                order.setYear(year);
                                memberOrderDao.save(order);
                                return Result.success(orderInfo);
                            } else {
                                result = new Result();
                                result.setMsg("下单失败");
                                result.setCode(0);
                                return result;
                            }

                    }
                } else {
                    result = new Result();
                    result.setCode(0);
                    result.setMsg("您已经是会员");
                    return result;
                }

            }
        }
        return null;
    }

    @Override
    public Result getMyRegistActivity(String token) {
        Long userId = getUserId(token);
        if(userId!=null){
            List<Map<String,Object>> registActivities = registActivityDao.getUserRegistActivity(userId);
            if(registActivities==null){
                return Result.failure(ResultCode.RESULE_DATA_NONE);
            }
            return Result.success(registActivities);
        }
        return Result.failure(ResultCode.USER_NOT_EXIST);
    }

    private Map getOrder(int money, User user, String outTradeNo) {
        OrderInfo order = new OrderInfo();
        order.setAppid(appId);
        order.setMch_id(mchId);
        String nonceStr = RandomStringGenerator.getRandomStringByLength(32);
        logger.info(nonceStr);
        order.setNonce_str(nonceStr);
        order.setBody("asong");
        order.setOut_trade_no(outTradeNo);
        order.setTotal_fee(money);
        order.setNotify_url(noticeUrl);
        order.setSpbill_create_ip(ip);
        order.setTrade_type("JSAPI");
        order.setOpenid(user.getOpenId());
        logger.info("用户下单openId:" + user.getOpenId());
        //生成签名
        try {
            Map<String, String> map = new HashMap<>();
            map.put("appid", order.getAppid());
            map.put("mch_id", order.getMch_id());
            map.put("nonce_str", order.getNonce_str());
            map.put("body", order.getBody());
            map.put("out_trade_no", order.getOut_trade_no());
            map.put("total_fee", order.getTotal_fee() + "");
            map.put("spbill_create_ip", order.getSpbill_create_ip());
            map.put("notify_url", order.getNotify_url());
            map.put("trade_type", order.getTrade_type());
            map.put("openid", user.getOpenId());
            String sign = WxPayUtil.getSign(map, key);
            map.put("sign", sign);
            logger.info("签名：" + sign);
            order.setSign(sign);
            String result1 = WxPayUtil.sendPost(map, payUrl, 1000000, 100000);
            logger.info("下单返回参数" + result1);
            logger.info("下单返回参数" + result1);
            Map<String, String> resultMap = WxPayUtil.xmlToMap(result1);
            logger.info("解析完毕：" + resultMap.toString());
            System.out.println(resultMap.get("result_code") + "," + resultMap.get("return_code"));
            // 二次签名
            if ("SUCCESS".equals(resultMap.get("result_code")) && resultMap.get("return_code").equals(resultMap.get("result_code"))) {
                Map<String, String> map1 = new HashMap<>();
                map1.put("appId", appId);
                long time = System.currentTimeMillis() / 1000;
                map1.put("timeStamp", String.valueOf(time));
                map1.put("nonceStr", RandomStringGenerator.getRandomStringByLength(32));
                map1.put("package", "prepay_id=" + resultMap.get("prepay_id"));
                map1.put("signType", "MD5");
                //生成签名
                String sign1 = WxPayUtil.getSign(map1, key);
                Map payInfo = new HashMap();
                payInfo.put("timeStamp", map1.get("timeStamp"));
                payInfo.put("nonceStr", map1.get("nonceStr"));
                payInfo.put("package", map1.get("package"));
                payInfo.put("signType", map1.get("signType"));
                payInfo.put("paySign", sign1);
                return payInfo;
            }

        } catch (IllegalAccessException e) {
            logger.error("签名生成异常");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long getUserId(String token) {
        String str = stringRedisTemplate.opsForValue().get(token);
        if (str != null) {
            String userId = redisService.getUserId(str);
            if (userId != null) {
                return Long.parseLong(userId);
            }
        }
        return null;
    }


    private String getRandomStringByLength(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * @return String
     * @function 生成商户订单号/退款单号
     * @date 2015-12-17
     */
    private String getOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        return sdf.format(date) + getRandomStringByLength(4);
    }
}
