package com.example.demo.area.aspect;

import com.example.demo.area.rateLimit.RateLimit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zx
 */
@Aspect
@Component
public class MysqlOperLogAspect {

    private final static Logger logger = LoggerFactory.getLogger(MysqlOperLogAspect.class);

    private ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();

    private static ObjectMapper objectMapper = new ObjectMapper();

    private RateLimiter rateLimiter;

    @Autowired
    private HttpServletResponse response;


    @Pointcut("execution(* com.example.demo.area.controller.*.*(..)) ")
    public void mysqlOperLog() {
    }

    @Before("mysqlOperLog()")
    public void beforeMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        logger.info("【前置通知】:" + className + "类的" + methodName + "方法开始");
        logger.info("【请求报文】:参数名:"  +Arrays.toString(argNames) + ",参数值:"+ Arrays.toString(joinPoint.getArgs()));
    }

    @After("mysqlOperLog()")
    public void afterMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        logger.info("【后置通知】: " + className + "类的" + methodName + "方法结束");
    }

    @AfterThrowing(value = "mysqlOperLog()", throwing = "except")
    public void afterThrowing(JoinPoint joinPoint, Exception except){
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        logger.info("【异常通知】: " + className + "类的" + methodName + "方法执行出现异常," + except);
    }

    @Around(value = "mysqlOperLog()")
    public Object aroundNotice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj = null;
        //获取拦截的方法名
        Signature sig = joinPoint.getSignature();
        //获取拦截的方法名
        MethodSignature msig = (MethodSignature) sig;
        //返回被织入增加处理目标对象
        Object target = joinPoint.getTarget();
        //为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        //获取注解信息
        RateLimit annotation = currentMethod.getAnnotation(RateLimit.class);
        double limitNum = annotation.limitNum(); //获取注解每秒加入桶中的token
        String functionName = msig.getName(); // 注解所在方法名区分不同的限流策略

        //获取rateLimiter
        if(map.containsKey(functionName)){
            rateLimiter = map.get(functionName);
        }else {
            map.put(functionName, RateLimiter.create(limitNum));
            rateLimiter = map.get(functionName);
        }

        try {
            if (rateLimiter.tryAcquire()) {
                //执行方法
                obj = joinPoint.proceed();
            } else {
                //拒绝了请求
                logger.info("服务器正忙，请稍后再试！" );
                obj="服务器正忙，请稍后再试！";
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }


    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }



}
