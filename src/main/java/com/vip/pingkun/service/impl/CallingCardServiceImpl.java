package com.vip.pingkun.service.impl;

import com.vip.pingkun.constant.Constant;
import com.vip.pingkun.dao.CallingCardDao;
import com.vip.pingkun.dao.SearchCardDao;
import com.vip.pingkun.dao.UserDao;
import com.vip.pingkun.pojo.CallingCard;
import com.vip.pingkun.pojo.SearchCard;
import com.vip.pingkun.pojo.User;
import com.vip.pingkun.service.CallingCarService;
import com.vip.pingkun.service.RedisService;
import com.vip.pingkun.util.Result;
import com.vip.pingkun.util.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CallingCardServiceImpl implements CallingCarService {
    @Autowired
    CallingCardDao callingCardDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisService redisService;
    @Autowired
    UserDao userDao;
    @Autowired
    SearchCardDao searchCardDao;
    Result result;
    @Override
    public Result searchCar(String title,String token) {
        Long userId = getUserId(token);
        if(userId!=null){
            User user = userDao.findUserById(userId);
            if(user.getState()==-1){
                return Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN);
            }
            if(user.getGrade()== Constant.NON_MEMBER){
                result = Result.failure(ResultCode.PERMISSION_NO_ACCESS);
                result.setMsg("非会员没有搜索权限");
                return result;
            }
            Date endDate = user.getEndGradeTime();
            Date nowDate = new Date();
            if(endDate.before(nowDate)){
                result = Result.failure(ResultCode.PERMISSION_NO_ACCESS);
                result.setMsg("您的会员已经到期");
                return result;
            }
            int yearCount = 0;
            int dayCount = 0;
            //天数据
            List<SearchCard> searchCards = searchCardDao.getToDaySerch(userId);
            if(searchCards!=null){
                dayCount = searchCards.size();
            }
            //年数据
            List<SearchCard> searchCards1 = searchCardDao.getYearSerch(userId);
            if(searchCards1!=null){
                yearCount = searchCards1.size();
            }
            //查询结果
            List<CallingCard> callingCards = callingCardDao.findCallingCardsByNameLikeOrCommpanyNameLikeOrPhoneLike("%"+title+"%","%"+title+"%","%"+title+"%");
            switch (user.getGrade()){
                case Constant.VIP_MEMBER:
                    if(yearCount>=1500){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今年查询次数受限");
                    }
                    if(dayCount>=20){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今天搜索次数受限");
                    }
                    break;
                case Constant.SILVER_MEMBER:
                    if(yearCount>=2500){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今年查询次数受限");
                    }
                    if(dayCount>=40){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今天搜索次数受限");
                    }
                    break;
                case Constant.GOLD_MEMBER:
                    if(yearCount>=3500){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今年查询次数受限");
                    }
                    if(dayCount>=60){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今天搜索次数受限");
                    }
                    break;
                case Constant.DLIAMOND_MEMBER:
                    if(yearCount>=4500){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今年查询次数受限");
                    }
                    if(dayCount>=80){
                        return Result.failure(ResultCode.PERMISSION_NO_ACCESS,"今天搜索次数受限");
                    }
                    break;
            }
            if(callingCards!=null&&callingCards.size()>0){
                SearchCard searchCard = new SearchCard();
                searchCard.setCreateTime(new Date());
                searchCard.setUserId(userId);
                searchCardDao.save(searchCard);
                return Result.success(callingCards);
            }
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }
        return Result.failure(ResultCode.USER_NOT_EXIST);
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
}
