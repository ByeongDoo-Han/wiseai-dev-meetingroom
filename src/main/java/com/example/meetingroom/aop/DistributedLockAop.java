package com.example.meetingroom.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";
    private final AopForTransaction aopForTransaction;
    private final RedissonClient redissonClient;

    @Around("@annotation(com.example.meetingroom.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String lockPrefix = distributedLock.lock();
        String key = lockPrefix + CustomSpringELParser.getDynamicValue(
            signature.getParameterNames(),
            joinPoint.getArgs(),
            distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit());
            if (!available) {
                return false;
            }
            Object result = aopForTransaction.proceed(joinPoint);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                        log.info("Redisson Lock unlocked after transaction completion: {}", key);
                    }
                }
            });
            return result;
        } catch (Exception e) {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("Redisson Lock unlocked due to exception: {}", key);
            }
            throw e;
        }
    }
}