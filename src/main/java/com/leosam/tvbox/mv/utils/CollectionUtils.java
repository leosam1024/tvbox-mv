package com.leosam.tvbox.mv.utils;

import java.util.Collection;

/**
 * @author admin
 * @since 2023/6/12 21:15
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

}
