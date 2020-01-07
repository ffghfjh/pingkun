package com.vip.pingkun.interceptor;

import com.vip.pingkun.util.JwtTokenUtil;
import com.vip.pingkun.util.PassToken;
import com.vip.pingkun.util.UserLoginToken;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 验证登录拦截器
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }


        String token = request.getHeader("token");
        logger.info("进入"+token);

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method =  handlerMethod.getMethod();


        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                logger.info("认证成功");
                return true;
            }
        }

        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if(userLoginToken.required()){
                //执行认证
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                try{
                    Claims clains = JwtTokenUtil.parseJWT(token);
                    if(clains.getIssuer().equals("dengshilin")){  //验证签发者
                        logger.info("认证成功");
                        return true;
                    }else {
                        logger.info("签发者验证失败");
                        return false;
                    }
                }catch (Exception e){
                    logger.info("认证失败");
                    return false;
                }
            }
        }
        logger.info("认证成功");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }


}
