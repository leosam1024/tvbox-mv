package com.leosam.tvbox.mv.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author admin
 * @since 2023/6/11 20:03
 */
public class MyTimeInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MyTimeInterceptor.class);
    /**
     * 线程变量 - 开始执行时间
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTime.set(System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (startTime.get() == null) {
            return;
        }
        long processTime = System.nanoTime() - startTime.get();
        if (logger.isInfoEnabled()) {
            logger.info("RequestURL[{}], Referer[{}], TIME[{} ms]", request.getRequestURL().toString(), request.getHeader("Referer"), String.format("%.3f", (processTime / 1_000_000.0)));
        }
        startTime.remove();
    }

}

