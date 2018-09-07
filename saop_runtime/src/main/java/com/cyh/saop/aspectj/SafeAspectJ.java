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

import com.cyh.saop.SAOP;
import com.cyh.saop.annotation.Safe;
import com.cyh.saop.logger.XLogger;
import com.cyh.saop.util.Utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * desc   : 自动try-catch的注解切片处理
 * author : cyh
 */
@Aspect
public class SafeAspectJ {

    @Pointcut("within(@com.cyh.saop.annotation.Safe *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.cyh.saop.annotation.Safe * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }  //方法切入点

    @Around("method() && @annotation(safe)")//在连接点进行方法替换
    public Object aroundJoinPoint(final ProceedingJoinPoint joinPoint, Safe safe) throws Throwable {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            if (SAOP.getIThrowableHandler() != null) {
                String flag = safe.value();
                if (TextUtils.isEmpty(flag)) {
                    flag = Utils.getMethodName(joinPoint);
                }
                result = SAOP.getIThrowableHandler().handleThrowable(flag, e);
            } else {
                XLogger.e(e);
                //默认不做任何处理
            }
        }
        return result;
    }
}
