package com.cyh.saop;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cyh.saop.cache.XCache;
import com.cyh.saop.cache.XDiskCache;
import com.cyh.saop.cache.XMemoryCache;
import com.cyh.saop.checker.IThrowableHandler;
import com.cyh.saop.checker.Interceptor;
import com.cyh.saop.logger.ILogger;
import com.cyh.saop.logger.XLogger;
import com.cyh.saop.util.PermissionUtils;
import com.cyh.saop.util.Strings;

/**
 * File: SAOP.java
 * Author: chenyihui
 */
public class SAOP {
    private static Context sContext;

    /**
     * 权限申请被拒绝的监听
     */
    private static PermissionUtils.OnPermissionDeniedListener sOnPermissionDeniedListener;

    /**
     * 自定义拦截切片的拦截器接口
     */
    private static Interceptor sInterceptor;

    /**
     * 自定义的异常处理者接口
     */
    private static IThrowableHandler sIThrowableHandler;

    /**
     * 初始化
     *
     * @param application
     */
    public static void init(Application application) {
        sContext = application.getApplicationContext();
    }

    /**
     * 获取全局上下文
     *
     * @return
     */
    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XAOP.init() 初始化！");
        }
    }

    //============动态申请权限失败事件设置=============//

    /**
     * 设置权限申请被拒绝的监听
     *
     * @param listener 权限申请被拒绝的监听器
     */
    public static void setOnPermissionDeniedListener(PermissionUtils.OnPermissionDeniedListener listener) {
        SAOP.sOnPermissionDeniedListener = listener;
    }

    public static PermissionUtils.OnPermissionDeniedListener getOnPermissionDeniedListener() {
        return sOnPermissionDeniedListener;
    }

    //============自定义拦截器设置=============//

    /**
     * 设置自定义拦截切片的拦截器接口
     *
     * @param sInterceptor 自定义拦截切片的拦截器接口
     */
    public static void setInterceptor(Interceptor sInterceptor) {
        SAOP.sInterceptor = sInterceptor;
    }

    public static Interceptor getInterceptor() {
        return sInterceptor;
    }

    //============自定义捕获异常处理=============//

    /**
     * 设置自定义捕获异常处理
     *
     * @param sIThrowableHandler 自定义捕获异常处理
     */
    public static void setIThrowableHandler(IThrowableHandler sIThrowableHandler) {
        SAOP.sIThrowableHandler = sIThrowableHandler;
    }

    public static IThrowableHandler getIThrowableHandler() {
        return sIThrowableHandler;
    }

    //============日志打印设置=============//

    /**
     * 设置是否打开调试
     *
     * @param isDebug
     */
    public static void debug(boolean isDebug) {
        XLogger.debug(isDebug);
    }

    /**
     * 设置调试模式
     *
     * @param tag
     */
    public static void debug(String tag) {
        XLogger.debug(tag);
    }

    /**
     * 设置打印日志的等级（只打印改等级以上的日志）
     *
     * @param priority
     */
    public static void setPriority(int priority) {
        XLogger.setPriority(priority);
    }

    /**
     * 设置日志打印时参数序列化的接口方法
     *
     * @param sISerializer
     */
    public static void setISerializer(Strings.ISerializer sISerializer) {
        XLogger.setISerializer(sISerializer);
    }

    /**
     * 设置日志记录者的接口
     *
     * @param logger
     */
    public static void setLogger(@NonNull ILogger logger) {
        XLogger.setLogger(logger);
    }

    //============缓存设置=============//

    /**
     * 初始化内存缓存
     *
     * @param memoryMaxSize
     */
    public static void initMemoryCache(int memoryMaxSize) {
        XMemoryCache.getInstance().init(memoryMaxSize);
    }

    /**
     * 初始化磁盘缓存
     *
     * @param builder
     */
    public static void initDiskCache(XCache.Builder builder) {
        XDiskCache.getInstance().init(builder);
    }
}
