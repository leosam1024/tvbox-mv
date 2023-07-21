package com.leosam.tvbox.mv.service;

import android.content.Context;
import com.github.catvod.crawler.Spider;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class T4Service {


    static {
        System.out.println(System.getProperty("sun.net.spi.nameservice.provider.1"));
        //use getaddrinfo
        System.setProperty("sun.net.spi.nameservice.provider.1", "default");
    }

    private static final ConcurrentHashMap<String, Spider> SPIDER = new ConcurrentHashMap<String, Spider>();


    public static Spider getSpider(String className, String ext) {

        Class<?> clazz = null;
        if (className != null && className.startsWith("csp_")) {
            className = className.substring(4);
        }
        try {
            clazz = Class.forName("com.github.catvod.spider." + className);
        } catch (ClassNotFoundException e) {

        }
        if (clazz == null || !Spider.class.isAssignableFrom(clazz)) {
            return null;
        }
        Spider spider = SPIDER.get(clazz.getName() + "_" + ext);
        synchronized (T4Service.class) {

            if (spider == null) {
                try {
                    spider = (Spider) clazz.getDeclaredConstructor().newInstance();
                    spider.init(Context.getInstance(), ext);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                SPIDER.put(clazz.getName() + "_" + ext, spider);
            }
        }
        return spider;

    }
}
