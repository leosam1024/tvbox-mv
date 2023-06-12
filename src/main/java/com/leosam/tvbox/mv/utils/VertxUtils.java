package com.leosam.tvbox.mv.utils;

import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * @author admin
 * @since 2023/6/12 21:16
 */
public class VertxUtils {

    public static String queryParam(RoutingContext context, String wd) {
        List<String> param = context.queryParam(wd);
        if (param != null && param.size() > 0) {
            return param.get(0);
        }
        return "";
    }
}
