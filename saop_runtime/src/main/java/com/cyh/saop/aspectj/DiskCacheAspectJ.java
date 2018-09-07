/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyh.saop.aspectj;

import android.text.TextUtils;

import com.cyh.saop.annotation.DiskCache;
import com.cyh.saop.cache.XDiskCache;
import com.cyh.saop.logger.XLogger;
import com.cyh.saop.util.Utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Collection;

/**
 * desc   : 磁盘缓存切片
 * author : cyh
 */
@Aspect
public class DiskCacheAspectJ {

    @Pointcut("within(@com.cyh.saop.annotation.DiskCache *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.cyh.saop.annotation.DiskCache * *(..)) || methodInsideAnnotatedType()")
    public void method() {
        //方法切入点
    }

    @Around("method() && @annotation(diskCache)")
    public Object aroundJoinPoint(ProceedingJoinPoint joinPoint, DiskCache diskCache) throws Throwable {
        //在连接点进行方法替换
        if (!Utils.isHasReturnType(joinPoint.getSignature())) {
            //没有返回值的方法，不进行缓存处理
            return joinPoint.proceed();
        }

        String key = diskCache.value();
        if (TextUtils.isEmpty(key)) {
            key = Utils.getCacheKey(joinPoint);
        }

        Object result = XDiskCache.getInstance().load(key, diskCache.cacheTime());
        XLogger.dTag("DiskCache", getCacheMsg(joinPoint, key, result));
        if (result != null) {
            //缓存已有，直接返回
            return result;
        }
        //执行原方法
        result = joinPoint.proceed();
        if (result != null) {
            //列表不为空
            if (result instanceof Collection && !((Collection) result).isEmpty()
                    || result instanceof String && !TextUtils.isEmpty((String) result)) {
                //字符不为空
                XDiskCache.getInstance().save(key, result);
                //存入缓存
                XLogger.dTag("DiskCache", "key：" + key + "--->" + "save ");
            }
        }
        return result;
    }

    /**
     * 获取缓存信息
     *
     * @param joinPoint
     * @param key       缓存key
     * @param value     缓存内容
     * @return
     */
    private String getCacheMsg(ProceedingJoinPoint joinPoint, String key, Object value) {
        return "key：" + key + "--->" + (value != null ? "not null, do not need to proceed method " + joinPoint.getSignature().getName() : "null, need to proceed method " + joinPoint.getSignature().getName());
    }
}
