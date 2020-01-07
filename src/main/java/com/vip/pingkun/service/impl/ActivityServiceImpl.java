package com.vip.pingkun.service.impl;

import com.vip.pingkun.bean.OrderInfo;
import com.vip.pingkun.dao.ActivityDao;
import com.vip.pingkun.dao.RegistActivityDao;
import com.vip.pingkun.dao.UserDao;
import com.vip.pingkun.pojo.InfoMation;
import com.vip.pingkun.pojo.RegistActivity;
import com.vip.pingkun.pojo.User;
import com.vip.pingkun.service.ActivityService;
import com.vip.pingkun.service.RedisService;
import com.vip.pingkun.util.RandomStringGenerator;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.ResultCode;
import com.vip.pingkun.util.WxPayUtil;
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
public class ActivityServiceImpl implements ActivityService {

    Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Value("${wxchat.appid}")
    String appId;
    @Value("${wxchat.secret}")
    String secret;
    @Value("${wxchat.mch_id}")
    String mchId;
    @Value("${ip}")
    String ip;
    @Value("${registNoticeUrl}")
    String registNoticeUrl;
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
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisService redisService;
    @Autowired
    ActivityDao activityDao;
    @Autowired
    UserDao userDao;
    @Autowired
    RegistActivityDao registActivityDao;
    Result result;

    /**
     * 报名活动并下单
     * @param activityId
     * @param needBill
     * @param token
     * @return
     */
    @Override
    public Result registActivity(Long activityId, boolean needBill, String token) {
        Long userId = getUserId(token);
        if(userId!=null){
            InfoMation info = activityDao.findInfoMationById(activityId);
            if(info==null){
                result = Result.failure(ResultCode.FAILURE);
                result.setMsg("未知活动");
                return result;
            }
            if(info.getState()==0){
                result = Result.failure(ResultCode.FAILURE);
                result.setMsg("活动未开启");
                return result;
            }
            User user = userDao.findUserById(userId);
            if(user!=null){
                if(user.getState() == -1){
                    return Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN);
                }
                String outTradeNo = getOrderNo();
                Map orderInfo = getOrder(info.getMoney(),user,outTradeNo);
                RegistActivity registActivity = new RegistActivity();
                registActivity.setActivityId(activityId);
                registActivity.setMoney(info.getMoney());
                registActivity.setUserId(userId);
                registActivity.setNeedBill(needBill);
                registActivity.setState(0);
                registActivity.setOrderNo(outTradeNo);
                registActivityDao.save(registActivity);
                return Result.success(orderInfo);
            }
        }
        return Result.success();
    }

    @Override
    public Result getHotActivitys() {
        List<InfoMation> infoMations = activityDao.getHotInfoMation();
        if(infoMations!=null){
            return Result.success(infoMations);
        }
        return Result.failure(ResultCode.RESULE_DATA_NONE);
    }


    private Map getOrder(int money, User user, String outTradeNo){
        OrderInfo order = new OrderInfo();
        order.setAppid(appId);
        order.setMch_id(mchId);
        String nonceStr = RandomStringGenerator.getRandomStringByLength(32);
        logger.info(nonceStr);
        order.setNonce_str(nonceStr);
        order.setBody("asong");
        order.setOut_trade_no(outTradeNo);
        order.setTotal_fee(money);
        order.setNotify_url(registNoticeUrl);
        order.setSpbill_create_ip(ip);
        order.setTrade_type("JSAPI");
        order.setOpenid(user.getOpenId());
        logger.info("用户下单openId:"+user.getOpenId());
        //生成签名
        try {
            Map<String,String> map = new HashMap<>();
            map.put("appid",order.getAppid());
            map.put("mch_id",order.getMch_id());
            map.put("nonce_str",order.getNonce_str());
            map.put("body",order.getBody());
            map.put("out_trade_no",order.getOut_trade_no());
            map.put("total_fee",order.getTotal_fee()+"");
            map.put("spbill_create_ip",order.getSpbill_create_ip());
            map.put("notify_url",order.getNotify_url());
            map.put("trade_type",order.getTrade_type());
            map.put("openid",user.getOpenId());
            String sign = WxPayUtil.getSign(map,key);
            map.put("sign",sign);
            logger.info("签名："+sign);
            order.setSign(sign);
            String result1 = WxPayUtil.sendPost(map,payUrl,1000000,100000);
            logger.info("下单返回参数"+result1);logger.info("下单返回参数"+result1);
            Map<String,String> resultMap = WxPayUtil.xmlToMap(result1);
            logger.info("解析完毕："+resultMap.toString());
            System.out.println(resultMap.get("result_code")+","+resultMap.get("return_code"));
            // 二次签名
            if ("SUCCESS".equals(resultMap.get("result_code")) && resultMap.get("return_code").equals(resultMap.get("result_code"))) {
                Map<String,String> map1 = new HashMap<>();
                map1.put("appId",appId);
                long time = System.currentTimeMillis()/1000;
                map1.put("timeStamp",String.valueOf(time));
                map1.put("nonceStr",RandomStringGenerator.getRandomStringByLength(32));
                map1.put("package","prepay_id="+resultMap.get("prepay_id"));
                map1.put("signType","MD5");
                //生成签名
                String sign1 = WxPayUtil.getSign(map1,key);
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
        }  catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long getUserId(String token){
        String str = stringRedisTemplate.opsForValue().get(token);
        if(str!=null){
            String userId = redisService.getUserId(str);
            if(userId!=null){
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
